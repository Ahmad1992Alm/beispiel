package beispiel;

/**
 * Simple 3D vector representation.
 */
public class Vector3d {
    private double x;
    private double y;
    private double z;

    public Vector3d() {
        this(0,0,0);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
}
