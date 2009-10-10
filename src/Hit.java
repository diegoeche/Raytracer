import javax.vecmath.*;

public class Hit {
	private double t = 0d;
	private Color3f color = null;
	
	public Hit() {
		// ToDo: initialize the value of t and
		// the color
		// ...
	}
	
	public Hit(double t, Color3f color) {
		super();
		this.t = t;
		this.color = color;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

	public Color3f getColor() {
		return color;
	}

	public void setColor(Color3f color) {
		this.color = color;
	}
	
}
