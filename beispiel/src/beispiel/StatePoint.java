package beispiel;

/**
 * Represents a state vertex in the graph with a position.
 */
public class StatePoint {
    private static int counter = 1; // shared across all instances

    private String id;
    private Vector3d position;

    public StatePoint() {
        this.id = "sp" + counter++;
        this.position = new Vector3d();
    }

    public StatePoint(Vector3d position) {
        this.id = "sp" + counter++;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }
}
