import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event._;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.j3d._;
import javax.media.j3d.{Material => JMaterial}
import javax.media.j3d.{Background => JBackground}
import javax.vecmath._;
import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.universe._; 
import com.sun.j3d.utils._; 

object Helpers {
  def plane(v: Vector3d, d: Double):Shape3D = {
    val v1 = new Vector3d;
    val v2 = new Vector3d;
    val t3dA = new Transform3D();
    t3dA.set(v)
    val t3dB = new Transform3D(t3dA);
    (v.x, v.y, v.z) match {
      case (x, 0.0, 0.0) if x * x == 1 => {
        t3dA.rotY(Math.Pi/2)
        t3dB.rotZ(Math.Pi/2)
      }
      case (0.0, y, 0.0) if y * y == 1 => {
        t3dA.rotX(Math.Pi/2)
        t3dB.rotZ(Math.Pi/2)
      }
      case _  => {
        t3dA.rotX(Math.Pi/2)
        t3dB.rotY(Math.Pi/2)
      }
    }
    t3dA.get(v1)
    t3dB.get(v2)
    // Point in plane
    val pointPlane = new Vector3d()
    pointPlane.scale(d,v) 

    val format = GeometryArray.COORDINATES;
    val stripCounts = Array(4);
    
    val tris = new TriangleStripArray(4, format, stripCounts);
    val vertices:Array[Double] = Array(-5,  1, -5,  
                                        5,  1, -5,  
                                       -5,  1,  5,  
                                        5,  1,  5)
    tris.setCoordinates(0, vertices);
    new Shape3D(tris);
  }
}

object Main extends Application {
  val applet = new Applet() {
    // Configuration
    setLayout(new BorderLayout());
    val config = SimpleUniverse.getPreferredConfiguration();
    val canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    val scene = createSceneGraph();
    parseScene();
    val simpleU = new SimpleUniverse(canvas3D);
 
    simpleU.getViewingPlatform().setNominalViewingTransform();
    simpleU.addBranchGraph(scene);
 
    def process(l: List[SceneElement]) = {
      l.foreach {
        case Background(c) => {
          val bounds = new BoundingSphere(new Point3d(), Math.MAX_DOUBLE)
          val backg = new JBackground (c) 
          backg.setApplicationBounds (bounds)
          scene.addChild (backg)
        }        
        case LightSource(l,c) => {
          // Lights
          val bounds = new BoundingSphere(new Point3d(), Math.MAX_DOUBLE);
          val ambientLgt = new AmbientLight(c);
          ambientLgt.setInfluencingBounds(bounds);
          val lColor1 = new Color3f (c) //Color3f(0.7f, 0.7f, 0.7f);
          val lDir1  = new Vector3f (l);
          val dirLgt = new DirectionalLight(lColor1, lDir1);
          dirLgt.setInfluencingBounds(bounds);
          // Add Lights
          scene.addChild(ambientLgt);
          scene.addChild(dirLgt);       
        }
        case Camera(l,la) => () // TODO
        case SceneObject(g,Material(pigment)) =>
          val tg = new TransformGroup()
          val pos = new Transform3D()
          val black = new Color3f(0.0f, 0.0f, 0.0f);
          val white = new Color3f(1.0f, 1.0f, 1.0f);
          val app = new Appearance ();
          val color = new Color3f(pigment)
          app.setMaterial ( new JMaterial (color,black,color,white,80.0f))
              
          g match {
            case Sphere(c, r) => {
              val sphere = new geometry.Sphere(r.toFloat, app)
              pos.set(c)
              tg.setTransform(pos)
              tg.addChild(sphere)
              scene.addChild(tg)
            }
        }
        case _ => ()
      }
    }

    def parseScene() = {
      val text = io.Source.fromPath("scene.txt").mkString
      SceneParser.parse(text) match {
        case Left(err) => {println(err); exit(0);}
        case Right(tree) => process(tree);
      }
    }

    def createSceneGraph(): BranchGroup = {
      val objRoot = new BranchGroup();
      return objRoot;
    } 
  }
  new MainFrame(applet, 256, 256);
}
