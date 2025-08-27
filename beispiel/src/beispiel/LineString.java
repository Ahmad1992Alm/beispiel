package beispiel;

import java.util.Arrays;
import java.util.List;

/**
 * Minimal LineString representation using a list of points.
 */
public class LineString {
    private final List<Vector3d> points;

    public LineString(Vector3d... pts) {
        this.points = Arrays.asList(pts);
    }

    public List<Vector3d> getPoints() {
        return points;
    }
}
