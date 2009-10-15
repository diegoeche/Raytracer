import javax.vecmath._;
import scala.Math._;

case class Material(pigment: Color3f)

class SceneElement
class Geometry() 

sealed case class Background  (color : Color3f)                        extends SceneElement
sealed case class Camera      (location: Vector3d, lookAt: Vector3d)   extends SceneElement
sealed case class LightSource (location: Vector3d, color: Color3f)     extends SceneElement
sealed case class SceneObject (geometry: Geometry, material: Material) extends SceneElement 
sealed case class Plane	      (point:Vector3d	 , distance: Double) extends Geometry
case class Sphere(center:Vector3d , radius: Double) extends Geometry {
 
    def intersect (r:Ray, h:Hit, range:Range,material:Color3f): Option [(Hit,Range)]=
    { 

      var v            = new Vector3d ( r.origin.x - this.center.x , r.origin.y - this.center.y,r.origin.z - this.center.z);
      var discriminant = pow (v.dot (r.direction),2.0) - (pow (v.dot(v),2) - pow (radius,2));
      if (discriminant >= 0 )
        {
          //var t1 = -(v.dot(r.direction)) + sqrt (discriminant);
           var t = -(v.dot(r.direction)) - sqrt (discriminant);
          
          if (t >= range.minT  && t < range.maxT )
            {
              var h      = new Hit (t,material);
              range.maxT = t;
              
              return Some (h,range);              
            }
          return None;
          
        }
      else              
        return None;
    }
 
}
