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
  def plane(v: Vector3d, d: Double, a: Appearance):Shape3D = {
    val v1 = new Vector3d(v);
    val v2 = new Vector3d(v);
    val t3dA = new Transform3D();
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
    t3dA.transform(v1)
    t3dB.transform(v2)

    v1.scale(1000)
    v2.scale(1000)

    // pp = Point in plane
    val pp = new Vector3d()
    pp.scale(d,v) 

    val format = GeometryArray.COORDINATES;
    val stripCounts = Array(4);
    val tris = new TriangleStripArray(4, format, stripCounts);

    val vertices:Array[Double] = 
      Array((pp.x - v1.x + v2.x),(pp.y - v1.y + v2.y),(pp.z - v1.z + v2.z),
            (pp.x + v1.x + v2.x),(pp.y + v1.y + v2.y),(pp.z + v1.z + v2.z),  
            (pp.x - v1.x - v2.x),(pp.y - v1.y - v2.y),(pp.z - v1.z - v2.z),  
            (pp.x + v1.x - v2.x),(pp.y + v1.y - v2.y),(pp.z + v1.z - v2.z))
    
    // for (v <- vertices) {
    //   println(v)
    // }
    
    tris.setCoordinates(0, vertices);
    val pa = new PolygonAttributes()
    pa.setCullFace(PolygonAttributes.CULL_NONE)
    a.setPolygonAttributes(pa)
    new Shape3D(tris, a);
  }
}

object Main extends Application {
  val applet = new Applet() {
    // Configuration
    setLayout(new BorderLayout());
    val config = SimpleUniverse.getPreferredConfiguration();
    val canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    val simpleU = new SimpleUniverse(canvas3D);
 
    simpleU.getViewingPlatform().setNominalViewingTransform();
    
    val scene = createSceneGraph();
    parseScene(); 
    simpleU.addBranchGraph(scene);
 
    def process(l: List[SceneElement]) = {
      l.foreach {
        case Background(c) =>
          {
            val bounds = new BoundingSphere(new Point3d(), Math.MAX_DOUBLE)
            val backg  = new JBackground (c) 
            backg.setApplicationBounds (bounds)
            scene.addChild (backg)
          }        
        case LightSource(l,c) => 
          {
            // Lightsl
            l.negate()
            val bounds     = new BoundingSphere(new Point3d(), Math.MAX_DOUBLE) 
//            val ambientLgt = new AmbientLight(c)                                
//            ambientLgt.setInfluencingBounds(bounds)                             
            val lColor1    = new Color3f (c)                                    
            val lDir1      = new Vector3f (l)                                   
            val ptLgt      = new PointLight(true,lColor1,new Point3f(lDir1),new Point3f (1,0,0))               
            ptLgt.setInfluencingBounds(bounds)                                 
            // Add Lights
//            scene.addChild(ambientLgt)
            scene.addChild(ptLgt)       
          }
        case Camera(l,la) => 
          {
            l.negate()
            la.negate()
            val camera = simpleU.getViewingPlatform().getViewPlatformTransform()
            val t3d    = new Transform3D()
            t3d.lookAt (new Point3d (l), new Point3d (la), new Vector3d (0.0,-1.0,0.0))
            t3d.setTranslation(l)            
            camera.setTransform (t3d)
            //val tg = new TransformGroup (t3d)
//            scene.addChild (tg)
            
            
          }
        case SceneObject(g,Material(pigment)) =>
          val tg    = new TransformGroup()
          val pos   = new Transform3D()
          val black = new Color3f(0.0f, 0.0f, 0.0f)
          val white = new Color3f(1.0f, 1.0f, 1.0f)
          val app   = new Appearance ()
          val color = new Color3f(pigment)
          app.setMaterial ( new JMaterial (color,black,color,white,80.0f))
              
          g match {
            case Sphere(c, r) => {
              c.negate()
              val sphere = new geometry.Sphere(r.toFloat, app)
              pos.set(c)
              tg.setTransform(pos)
              tg.addChild(sphere)
              scene.addChild(tg)
            }
            case Plane(c, r) => {
              c.negate()
              val plane = Helpers.plane(c, r.toFloat, app)
              scene.addChild(plane)
            }
        }
        case _ => ()
      }
    }

    def createSceneGraph(): BranchGroup = {
      val objRoot = new BranchGroup();
      
      //objRoot.addChild (simpleU.getViewingPlatform().getViewPlatformTransform())
      return objRoot;
    }

    def parseScene() = {
      val text = io.Source.fromPath("scene.pov").mkString
      SceneParser.parse(text) match {
        case Left(err)   => {println(err); exit(0);}
        case Right(tree) => process(tree);
      }
    }

   
  }
  new MainFrame(applet, 256, 256);
}
