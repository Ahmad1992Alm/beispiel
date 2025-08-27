package beispiel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX application that visualizes an IndoorGML graph.
 */
public class GraphApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String filePath = getParameters().getUnnamed().isEmpty()
                ? "indoor.gml"
                : getParameters().getUnnamed().get(0);
        IndoorGMLGraph graph = IndoorGMLGraph.fromFile(new File(filePath));

        Pane root = new Pane();
        int n = graph.getCellSpaces().size();
        double centerX = 300;
        double centerY = 300;
        double radius = 250;

        Map<StatePoint, Circle> stateCircles = new HashMap<>();

        final Circle[] selected = new Circle[1];
        for (int i = 0; i < n; i++) {
            CellSpace cell = graph.getCellSpaces().get(i);
            StatePoint state = cell.getState();
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            Circle circle = new Circle(x, y, 15);
            circle.setStroke(Color.BLACK);
            circle.setFill(Color.LIGHTGRAY);
            Text text = new Text(x - 10, y - 20, cell.getId());

            state.setPosition(new Vector3d(x, y, 0));

            final Delta dragDelta = new Delta();

            circle.setOnMousePressed(e -> {
                dragDelta.x = circle.getCenterX() - e.getX();
                dragDelta.y = circle.getCenterY() - e.getY();
                if (selected[0] != null) {
                    selected[0].setStroke(Color.BLACK);
                }
                selected[0] = circle;
                circle.setStroke(Color.RED);
            });

            circle.setOnMouseDragged(e -> {
                double newX = e.getX() + dragDelta.x;
                double newY = e.getY() + dragDelta.y;
                circle.setCenterX(newX);
                circle.setCenterY(newY);
                text.setX(newX - 10);
                text.setY(newY - 20);
                state.setPosition(new Vector3d(newX, newY, 0));
            });

            stateCircles.put(state, circle);
            root.getChildren().addAll(circle, text);
        }

        for (Transition tr : graph.getTransitions()) {
            Circle c1 = stateCircles.get(tr.getStateA());
            Circle c2 = stateCircles.get(tr.getStateB());
            if (c1 != null && c2 != null) {
                Line line = new Line();
                line.startXProperty().bind(c1.centerXProperty());
                line.startYProperty().bind(c1.centerYProperty());
                line.endXProperty().bind(c2.centerXProperty());
                line.endYProperty().bind(c2.centerYProperty());
                tr.setFxLine(line);
                root.getChildren().add(0, line);
            }
        }

        stage.setScene(new Scene(root, 600, 600));
        stage.setTitle("IndoorGML Graph");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Delta {
        double x, y;
    }
}
