/**
 * 
 */
import javax.vecmath.*;

/**
 * @author htrefftz
 *
 */
public class Main {
	int		size = 300;
	Group group = null;
	Image	image = null;
	Camera	camera = null;
	
	/**
	 * The objects of the scene are created here
	 */
	public void createScene() {
		// Create a red sphere
		Sphere s1 = new Sphere(new Point3d(0.5d, 0.5d, -0.5d), 0.3d, 
				new Color3f(1f, 0f, 0f));	
		// Create a blue sphere
		Sphere s2 = new Sphere(new Point3d(0.1d, 0.5d, -1.5d), 0.3d, 
				new Color3f(0f, 0f, 1f));	
		// Now create a group
		group = new Group();
		group.add(s1);
		group.add(s2);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main m = new Main();
		m.createScene();
		
		m.image = new Image(m.size, m.size);
		m.camera = new OrtographicCamera(1d);
		
		// ToDo: For each pixel in the image,
		// ask the camera to cast a ray
		// then find if there are intersections of the
		// ray with all objects in the scene
		// Set the color of the image when necessary
		for (int x = 0; x < m.size; x++) {
			for (int y = 0; y < m.size; y++) {
				//...
			}
		}
		// ToDo: Write the image at the end
		// ...
	}

}
