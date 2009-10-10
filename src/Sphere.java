import javax.vecmath.*;

public class Sphere extends Object3D {
	private Point3d center = null;
	private double radius = 1d;
	private Color3f color = null;
	boolean DEBUG = false;

	public Sphere() {
		this.center = new Point3d();
		this.radius = 1d;
		this.color = new Color3f(1f, 1f, 1f);
	}
	
	public Sphere(Point3d center, double radius, Color3f color) {
		super();
		this.center = center;
		this.radius = radius;
		this.color  = color;
	}

	boolean intersect(Ray r, Hit h, Range range, TValue tMin) {
		// ToDo:
		// Compute the intersection of the ray with the
		// Sphere and update what needs to be updated
		// Valid values for t must be within the "range"
		boolean retVal = false;
		// ...
		return retVal;
	}
	
}
