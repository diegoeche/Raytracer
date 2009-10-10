import javax.vecmath.*;
import java.io.*;

public class Image {
	int width;
	int height;
	Point3i [][] image = null;
	
	public Image(int width, int height) {
		this.width = width;
		this.height = height;
		image = new Point3i[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image[i][j] = new Point3i(0, 0, 0);
			}
		}
	}
	
	public void setColor(int x, int y, Color3f color) {
		Point3i myColor = new Point3i((int)(color.x * 255), 
				(int)(color.y * 255), (int)(color.z * 255));
		image[x][y] = myColor;
	}
	
	public void writeImageStdOutput() {
		System.out.println("P3");
		System.out.println("# Created by me");
		System.out.println(width + " " + height);
		System.out.println("255");
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				System.out.print(image[j][i].x + " " + 
						image[j][i].y + " " + 
						image[j][i].z + " ");
			}
			System.out.println();
		}		
	}
	
	public void writeImage() {
		PrintWriter printWriter = null;
		try {
			printWriter = 
				new PrintWriter(new BufferedWriter(new FileWriter(new File("image.ppm"))));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		printWriter.println("P3");
		printWriter.println("# Created by me");
		printWriter.println(width + " " + height);
		printWriter.println("255");
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				printWriter.print(image[j][i].x + " " + 
						image[j][i].y + " " + 
						image[j][i].z + " ");
			}
			printWriter.println();
		}		
		
	}

}
