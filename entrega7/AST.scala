import javax.vecmath._;
import scala.Math._;

case class Material(pigment: Color3f, ka: Float, kd: Float, ks: Float, kn:Float)

class SceneElement
abstract class Geometry {
  def intersect (r:Ray, range:Range, material:Material): Option [Hit];
}

sealed case class Background   (color : Color3f)                        extends SceneElement
sealed case class Camera       (location: Vector3d, lookAt: Vector3d)   extends SceneElement
sealed case class LightSource  (location: Vector3d, color: Color3f)     extends SceneElement
sealed case class AmbientLight (color : Color3f)                        extends SceneElement
sealed case class SceneObject  (geometry: Geometry, material: Material) extends SceneElement
sealed case class Plane        (point:Vector3d	 , distance: Double)   extends Geometry {

  override def intersect (r:Ray, range:Range, material: Material): Option [Hit] = None

}
case class Sphere(center:Vector3d, radius: Double)                    extends Geometry {

  override def intersect (r:Ray, range:Range, material: Material): Option [Hit]=
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
              val point = new Vector3d(r.direction)
              point.scale(t)
              point.add(r.origin)
              val normal = new Vector3d(point)
              normal.sub(this.center)
              normal.normalize()
              range.maxT = t

              Some(new Hit(t, normal, point, material))
            }
          }
        }
      else
        return None;
    }

}

// liftM2 (&&) (>=range.minT) (<range.maxT)
// (&&) <$> (>=range.minT) <*> (<range.maxT)
// liftA2 (&&) (>=range.minT) (<range.maxT)
// import Applicative.Infix
// (>=range.minT) <^ (&&) ^> (<range.maxT)
// instance Monad (->) where
// liftM a -> b -> m a -> m b
// liftM2 a -> b -> c -> ma ...
