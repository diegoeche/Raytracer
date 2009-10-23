import java.util.Vector;
import javax.vecmath.Color3f;
import java.awt.Color;
import scala.Math.MAX_DOUBLE;

case class Group (var vector:List [SceneElement] )
{
  def intersect( ray:Ray ):Hit = {
    var hit     = new Hit (-1, null, null);
    var range   = new Range (0.0,MAX_DOUBLE); 
    var sceneObjects = vector filter (_.isInstanceOf[SceneObject])
    sceneObjects.foreach {            
      case SceneObject (g, m) => {
        g.intersect(ray,range,m) match {
          case Some (nh) => (hit = nh)
          case _ => ()
        
        }

      }
    }
    return hit;
  }
}
