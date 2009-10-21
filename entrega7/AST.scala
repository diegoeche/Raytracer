import javax.vecmath._;
import scala.Math._;

case class Material(pigment: Color3f, ka: Double, kd: Double, ks: Double, n:Double)

class SceneElement
class Geometry() 

sealed case class Background  (color : Color3f)                        extends SceneElement
sealed case class Camera      (location: Vector3d, lookAt: Vector3d)   extends SceneElement
sealed case class LightSource (location: Vector3d, color: Color3f)     extends SceneElement
sealed case class AmbientLight (color : Color3f)                        extends SceneElement
sealed case class SceneObject (geometry: Geometry, material: Material) extends SceneElement 
 sealed case class Plane	      (point:Vector3d	 , distance: Double)   extends Geometry
case class Sphere(center:Vector3d , radius: Double)                    extends Geometry {
 
    def intersect (r:Ray, range:Range, material:Color3f): Option [Hit]=
    { 
      var v            = new Vector3d ( r.origin.x - (center.x) , r.origin.y - (center.y),r.origin.z - (center.z ));

      val dbg1 = pow (v.dot (r.direction),2.0)
      val dbg2 = ((v.dot(v)) - pow (radius,2))

      var discriminant = pow (v.dot (r.direction),2.0) - ((v.dot(v)) - pow (radius,2));      

      if (discriminant >= 0 )
        {
          var t1 = -(v.dot(r.direction)) + sqrt (discriminant);
          var t2 = -(v.dot(r.direction)) - sqrt (discriminant);
          List(t1, t2) filter (t => t >= range.minT && t < range.maxT) match {
            case List() => None
            case l      => {
              val t = l.min
              range.maxT = t
              Some(new Hit(t,material))
            }
          }
        }
      else              
        return None;
    }
 
}
