import javax.vecmath.*;

public class Ray {
	private Point3d origin = null;
	private Vector3d direction = null;
	
	public Ray() {
		origin = new Point3d();
		direction = new Vector3d();
	}
	
	public Ray(Point3d origin, Vector3d direction) {
		this.origin = origin;
		this.direction = direction;
		this.direction.normalize();
	}

	public Point3d getOrigin() {
		return origin;
	}

	public void setOrigin(Point3d origin) {
		this.origin = origin;
	}

	public Vector3d getDirection() {
		return direction;
	}

	public void setDirection(Vector3d direction) {
		this.direction = direction;
	}
	
}
