package beispiel;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Polygon;

/**
 * Representation of a CellSpace containing geometry and a state point.
 */
public class CellSpace {
    private static int counter = 1;

    private String id;
    private List<Polygon> polygons;
    private StatePoint state;
    private String stateRef;
    private List<String> level;
    private Polygon floorPolygon; // Cache for floor polygon

    public CellSpace() {
        this.id = "cs" + counter++;
        this.polygons = new ArrayList<>();
        this.level = new ArrayList<>();
    }

    public String getId() { return id; }
    public List<Polygon> getPolygons() { return polygons; }
    public StatePoint getState() { return state; }
    public void setState(StatePoint state) { this.state = state; }
    public String getStateRef() { return stateRef; }
    public void setStateRef(String stateRef) { this.stateRef = stateRef; }
    public List<String> getLevel() { return level; }
    public Polygon getFloorPolygon() { return floorPolygon; }
    public void setFloorPolygon(Polygon floorPolygon) { this.floorPolygon = floorPolygon; }
}
