import javax.vecmath._;
import scala.Math._;
import NiceVector._;

case class Material(pigment: Color3f,
                    ka: Float,
                    kd: Float,
                    ks: Float,
                    kn: Float,
                    kr: Float,
                    reflection: Float,
                    refraction: Double)

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

  val epsilon = 0.0000001

  override def intersect (r:Ray, range:Range, material: Material): Option [Hit]= {
    var v:NiceVector = r.origin - center

    var discriminant = pow (v * r.direction, 2.0) - ((v * v) - (radius * radius));

    if (discriminant >= 0 ) {
      val dot = - (v * r.direction)
      var t1 = dot + sqrt (discriminant);
      var t2 = dot - sqrt (discriminant);
      List(t1, t2) filter (t => t > (range.minT + epsilon) && 
                                t < range.maxT) match {
        case List() => None
        case l      => {
          val t = l.min
          val point: NiceVector = ((r.direction:NiceVector) scale t) + r.origin
          val normal = (point - this.center).normalize
          val c = (this.center - r.origin)
          val c2 = Math.sqrt(c * c)
          if (c2 < (radius + epsilon )) {
            normal.v.negate()
          }
          range.maxT = t
          Some(new Hit(t, normal.v, point.v, material))
        }
      }
    }
    else
      return None;
  }
}

case class Ray   (var origin:Vector3d, var direction:Vector3d )
case class Range (var minT:Double,    var maxT:Double )
case class Hit   (var t:Double,
                  var normal: Vector3d,
                  var location: Vector3d,
                  var material: Material)
