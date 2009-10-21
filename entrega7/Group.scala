import java.util.Vector;
import javax.vecmath.Color3f;
import java.awt.Color;
import scala.Math.MAX_DOUBLE;

case class Group (var vector:List [SceneElement] )
{
  
  def intersect( ray:Ray ):Hit = {
    var hit     = new Hit (-1,new Color3f (Color.black));
    var range   = new Range (0.0,MAX_DOUBLE); 
    
    vector.foreach {            
      case  SceneObject (g,Material (pigment, ka: Double, kd: Double, ks: Double, n:Double)) =>
        
        g match {
          case s:Sphere  => {
            
            s.intersect(ray,range,pigment) match{
              case Some (nh) =>
                {
                  hit =  nh;
                }
              case _ => ()
            }
          }
          case _ => ()
        }
      case _ => ()
    }
    return hit;
  }
}
