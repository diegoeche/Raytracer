import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event._;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.j3d._;
import javax.vecmath._;
import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.universe._; 
import com.sun.j3d.utils.geometry._; 

object Main extends Application {
  val applet = new Applet() {
    // Configuration
    setLayout(new BorderLayout());
    val config = SimpleUniverse.getPreferredConfiguration();
    val canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    val scene = createSceneGraph();
    val simpleU = new SimpleUniverse(canvas3D);
    simpleU.getViewingPlatform().setNominalViewingTransform();
    simpleU.addBranchGraph(scene);
    
    def createSceneGraph(): BranchGroup = {
      val objRoot = new BranchGroup();
      // Lights
      val bounds = new BoundingSphere(new Point3d(), Math.MAX_DOUBLE);
      val ambientLgt = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));

      ambientLgt.setInfluencingBounds(bounds);
      
      val lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
      val lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
      val dirLgt = new DirectionalLight(lColor1, lDir1);
      dirLgt.setInfluencingBounds(bounds);
      
      // Add Lights
      objRoot.addChild(ambientLgt);
      objRoot.addChild(dirLgt);
      
      return objRoot;
    } 
  }
  

  new MainFrame(applet, 256, 256);
}
