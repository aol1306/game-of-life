import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {

    Canvas canvas;
    ArrayList<ArrayList<Integer>> state; // game state
    static ArrayList<Integer> zeroRules = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
    static ArrayList<Integer> oneRules = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
    boolean running; // thread stopper

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // clear canvas
        gc.clearRect(0,0,800,800);
        gc.setLineWidth(2.0);
        gc.setStroke(Color.BLUE);

        for (int i=0; i<state.size(); i++) {
            for (int j=0; j<state.get(i).size(); j++) {
                int x = j*20;
                int y = i*20;
                if (state.get(i).get(j) == 0) {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(x, y,20,20);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x, y,20,20);
                }
                gc.strokeRect(x, y,20,20);
            }
        }
    }

    public void update() {
        // recalculate game state

        // calculate neighbourhood matrix
        ArrayList<ArrayList<Integer>> neighbourhoodMatrix = new ArrayList<>();
        for(int i=0; i<40; i++) {
            neighbourhoodMatrix.add(new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        }

        // center (no borders)
        for(int i=1; i<state.size()-1; i++) {
            neighbourhoodMatrix.add(new ArrayList<>());
            for (int j=1; j<state.get(i).size()-1; j++) {
                int sum = state.get(i - 1).get(j - 1) +
                        state.get(i).get(j - 1) +
                        state.get(i + 1).get(j - 1) +
                        state.get(i - 1).get(j + 1) +
                        state.get(i).get(j + 1) +
                        state.get(i + 1).get(j + 1) +
                        state.get(i - 1).get(j)  +
                        state.get(i + 1).get(j);
                neighbourhoodMatrix.get(i).set(j, sum);
            }
        }
        // top border (no corner)
        for (int j=1; j<state.get(0).size()-1; j++) {
            int sum = state.get(1).get(j - 1) +
                    state.get(1).get(j) +
                    state.get(1).get(j + 1) +
                    state.get(39).get(j - 1) +
                    state.get(39).get(j) +
                    state.get(39).get(j + 1) +
                    state.get(0).get(j - 1) +
                    state.get(0).get(j + 1);
            neighbourhoodMatrix.get(0).set(j, sum);
        }
        // bottom border (no corner)
        for (int j=1; j<state.get(39).size()-1; j++) {
            int sum = state.get(38).get(j - 1) +
                    state.get(38).get(j) +
                    state.get(38).get(j + 1) +
                    state.get(0).get(j - 1) +
                    state.get(0).get(j) +
                    state.get(0).get(j + 1) +
                    state.get(39).get(j - 1) +
                    state.get(39).get(j + 1);
            neighbourhoodMatrix.get(39).set(j, sum);
        }
        // right border (no corner)
        for (int i=1; i<state.size()-1; i++) {
            int sum = state.get(i - 1).get(0) +
                    state.get(i + 1).get(0) +
                    state.get(i - 1).get(1) +
                    state.get(i).get(1) +
                    state.get(i + 1).get(1) +
                    state.get(i - 1).get(39) +
                    state.get(i).get(39) +
                    state.get(i + 1).get(39);
            neighbourhoodMatrix.get(i).set(0, sum);
        }
        // left border (no corner)
        for (int i=1; i<state.size()-1; i++) {
            int sum = state.get(i - 1).get(39) +
                    state.get(i + 1).get(39) +
                    state.get(i - 1).get(38) +
                    state.get(i).get(38) +
                    state.get(i + 1).get(38) +
                    state.get(i - 1).get(0) +
                    state.get(i).get(0) +
                    state.get(i + 1).get(0);
            neighbourhoodMatrix.get(i).set(39, sum);
        }
        // top left
        int topLeftSum = state.get(0).get(1) +
                state.get(1).get(1) +
                state.get(1).get(0) +
                state.get(39).get(0) +
                state.get(39).get(1) +
                state.get(39).get(39) +
                state.get(0).get(39) +
                state.get(1).get(39);
        neighbourhoodMatrix.get(0).set(0, topLeftSum);
        // top right
        int topRightSum = state.get(0).get(0) +
                state.get(0).get(38) +
                state.get(1).get(0) +
                state.get(1).get(38) +
                state.get(1).get(39) +
                state.get(39).get(0) +
                state.get(39).get(38) +
                state.get(39).get(39);
        neighbourhoodMatrix.get(0).set(39, topRightSum);
        // bottom left
        int bottomLeftSum = state.get(0).get(0) +
                state.get(0).get(1) +
                state.get(0).get(39) +
                state.get(38).get(0) +
                state.get(38).get(1) +
                state.get(38).get(39) +
                state.get(39).get(1) +
                state.get(39).get(39);
        neighbourhoodMatrix.get(39).set(0, bottomLeftSum);
        // bottom right
        int bottomRightSum = state.get(0).get(0) +
                state.get(0).get(38) +
                state.get(0).get(39) +
                state.get(38).get(0) +
                state.get(38).get(38) +
                state.get(38).get(39) +
                state.get(39).get(0) +
                state.get(39).get(38);
        neighbourhoodMatrix.get(39).set(39, bottomRightSum);

                // recalculate game state based on neighbourhood matrix
        ArrayList<ArrayList<Integer>> newState = new ArrayList<>();
        for (int i=0; i<40; i++) {
            newState.add(new ArrayList<>());
            for (int j=0; j<40; j++) {
                newState.get(i).add(calculateNewValueFromRules(this.state.get(i).get(j), neighbourhoodMatrix.get(i).get(j)));
            }
        }

        // update game state
        this.state = newState;
    }

    public Integer calculateNewValueFromRules(Integer old, Integer neighbourCount) {
        if (old == 1) {
            return oneRules.get(neighbourCount);
            /*if (neighbourCount == 2 || neighbourCount == 3) {
                return 1;
            }*/
        } else {
            return zeroRules.get(neighbourCount);
            //if (neighbourCount == 3) return 1;
        }
    }

    public void init() {
        this.canvas = new Canvas(800,800);
        this.state = new ArrayList<>();
        for(int i=0; i<40; i++) {
            this.state.add(new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane root = new FlowPane();

        canvas.setOnMouseClicked(e -> {
            int y = (int)Math.floor(e.getX()/20);
            int x = (int)Math.floor(e.getY()/20);
            if (state.get(x).get(y) == 1) {
                this.state.get(x).set(y, 0);
            } else {
                this.state.get(x).set(y, 1);
            }
            draw();
        });

        Button updateBtn = new Button();
        updateBtn.setOnAction(e -> {
            update();
            draw();
        });
        updateBtn.setText("Update");

        Button playBtn = new Button();
        playBtn.setOnAction(e -> {
            Thread t = new Thread(() -> {
                while(this.running == true) {
                    update();
                    draw();
                    try {
                        Thread.sleep(1000 / 5); // 5 times per sec
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                        return;
                    }
                }
            });
            this.running = true;
            t.start();
        });
        playBtn.setText("Play");

        Button stopBtn = new Button();
        stopBtn.setOnAction(e -> {
            this.running = false; // stop thread
        });
        stopBtn.setText("Stop");

        // hbox for checkboxes
        HBox checkboxesPane = new HBox();
        for(int i=0; i<9; i++) {
            checkboxesPane.getChildren().add(new OneRulesCheckBox(i));
        }
        for(int i=0; i<9; i++) {
            checkboxesPane.getChildren().add(new ZeroRulesCheckBox(i));
        }

        root.getChildren().add(canvas);
        root.getChildren().add(updateBtn);
        root.getChildren().add(playBtn);
        root.getChildren().add(stopBtn);
        root.getChildren().add(checkboxesPane);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Game of Life");

        draw();

        primaryStage.show();
    }
}

class ZeroRulesCheckBox extends CheckBox {
    public int n;
    ZeroRulesCheckBox(int n) {
        super();
        this.n = n;

        this.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        this.setOnAction(e -> {
            ZeroRulesCheckBox rulesCheckBox = (ZeroRulesCheckBox)e.getSource();
            if(rulesCheckBox.isSelected()) {
                Main.zeroRules.set(n, 1);
            } else {
                Main.zeroRules.set(n, 0);
            }
            System.out.println(Main.zeroRules);
        });
    }
}

class OneRulesCheckBox extends CheckBox {
    public int n;
    OneRulesCheckBox(int n) {
        super();
        this.n = n;

        this.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        this.setOnAction(e -> {
            OneRulesCheckBox rulesCheckBox = (OneRulesCheckBox)e.getSource();
            if(rulesCheckBox.isSelected()) {
                Main.oneRules.set(n, 1);
            } else {
                Main.oneRules.set(n, 0);
            }
            System.out.println(Main.oneRules);
        });
    }
}