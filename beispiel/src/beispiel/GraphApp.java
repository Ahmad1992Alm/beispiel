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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * JavaFX application that visualizes an IndoorGML graph.
 */
public class GraphApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String filePath = getParameters().getUnnamed().isEmpty()
                ? "indoor.gml"
                : getParameters().getUnnamed().get(0);
        IndoorGMLParser.ParsedData data = IndoorGMLParser.parse(new File(filePath));
        List<CellSpace> cellSpaces = data.getCellSpaces();
        Map<String, List<String>> adjacencyGraph = data.getAdjacencyGraph();

        Pane root = new Pane();
        Map<String, Circle> cellCircles = new HashMap<>();

        // Map cell IDs to their objects for quick lookup
        Map<String, CellSpace> cellMap = new HashMap<>();
        for (CellSpace cs : cellSpaces) {
            cellMap.put(cs.getId(), cs);
        }

        // Compute simple tree levels starting from the first cell space
        String rootId = cellSpaces.isEmpty() ? null : cellSpaces.get(0).getId();
        Map<String, Integer> levels = computeLevels(adjacencyGraph, rootId);
        Map<Integer, List<String>> levelNodes = new HashMap<>();
        int maxLevel = 0;
        for (Map.Entry<String, Integer> e : levels.entrySet()) {
            levelNodes.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
            maxLevel = Math.max(maxLevel, e.getValue());
        }

        double width = 600;
        double verticalSpacing = 100;
        root.setPrefSize(width, (maxLevel + 2) * verticalSpacing);

        final Circle[] selected = new Circle[1];
        for (int level = 0; level <= maxLevel; level++) {
            List<String> ids = levelNodes.get(level);
            if (ids == null) continue;
            double horizontalSpacing = width / (ids.size() + 1);
            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                CellSpace cell = cellMap.get(id);
                StatePoint state = cell.getState();
                double x = horizontalSpacing * (i + 1);
                double y = verticalSpacing * (level + 1);
                Circle circle = new Circle(x, y, 15);
                circle.setStroke(Color.BLACK);
                circle.setFill(Color.LIGHTGRAY);
                Text text = new Text(x - 10, y - 20, id);

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

                cellCircles.put(id, circle);
                root.getChildren().addAll(circle, text);
            }
        }

        for (Map.Entry<String, List<String>> entry : adjacencyGraph.entrySet()) {
            String idA = entry.getKey();
            Circle c1 = cellCircles.get(idA);
            if (c1 == null) continue;
            for (String idB : entry.getValue()) {
                if (idA.compareTo(idB) < 0) {
                    Circle c2 = cellCircles.get(idB);
                    if (c2 != null) {
                        Line line = new Line();
                        line.startXProperty().bind(c1.centerXProperty());
                        line.startYProperty().bind(c1.centerYProperty());
                        line.endXProperty().bind(c2.centerXProperty());
                        line.endYProperty().bind(c2.centerYProperty());
                        root.getChildren().add(0, line);
                    }
                }
            }
        }

        stage.setScene(new Scene(root, 600, 600));
        stage.setTitle("IndoorGML Graph");
        stage.show();
    }

    private Map<String, Integer> computeLevels(Map<String, List<String>> adjacencyGraph, String rootId) {
        Map<String, Integer> levels = new HashMap<>();
        if (rootId == null) {
            return levels;
        }
        Queue<String> queue = new ArrayDeque<>();
        levels.put(rootId, 0);
        queue.add(rootId);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int level = levels.get(current);
            for (String neighbor : adjacencyGraph.getOrDefault(current, Collections.emptyList())) {
                if (!levels.containsKey(neighbor)) {
                    levels.put(neighbor, level + 1);
                    queue.add(neighbor);
                }
            }
        }
        return levels;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Delta {
        double x, y;
    }
}
