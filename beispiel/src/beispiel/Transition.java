package beispiel;

import javafx.scene.shape.Line;

/**
 * Represents a transition edge between two state points.
 */
public class Transition {
    private static int counter = 1;

    private String id;
    private StatePoint stateA;
    private StatePoint stateB;
    private LineString geometry;
    private Line fxLine; // reference to JavaFX Line

    public Transition(StatePoint a, StatePoint b) {
        this.id = "tr" + counter++;
        this.stateA = a;
        this.stateB = b;
    }

    public String getId() { return id; }
    public StatePoint getStateA() { return stateA; }
    public StatePoint getStateB() { return stateB; }
    public LineString getGeometry() { return geometry; }
    public void setGeometry(LineString geometry) { this.geometry = geometry; }
    public Line getFxLine() { return fxLine; }
    public void setFxLine(Line fxLine) { this.fxLine = fxLine; }
}
