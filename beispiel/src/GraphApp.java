package beispiel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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
        File gmlFile = new File(filePath);
        if (!gmlFile.exists()) {
            System.err.println("IndoorGML file not found: " + gmlFile.getAbsolutePath());
            Platform.exit();
            return;
        }
        IndoorGMLGraph graph = IndoorGMLGraph.fromFile(gmlFile);

        Pane root = new Pane();
        int n = graph.getNodes().size();
        double centerX = 300;
        double centerY = 300;
        double radius = 250;

        Map<IndoorGMLGraph.GraphNode, Point2D> positions = new HashMap<>();
        for (int i = 0; i < n; i++) {
            IndoorGMLGraph.GraphNode node = graph.getNodes().get(i);
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.put(node, new Point2D(x, y));
            Circle circle = new Circle(x, y, 15);
            Text text = new Text(x - 10, y - 20, node.label);
            root.getChildren().addAll(circle, text);
        }

        for (IndoorGMLGraph.GraphEdge edge : graph.getEdges()) {
            Point2D p1 = positions.get(edge.from);
            Point2D p2 = positions.get(edge.to);
            if (p1 != null && p2 != null) {
                Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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
}
