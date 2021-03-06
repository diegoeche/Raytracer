import java.util.Vector;
import javax.vecmath.Color3f;
import java.awt.Color;
import scala.Math.MAX_DOUBLE;

case class Group (var vector:List [SceneElement] )
{
  def intersect(range: Range, ray:Ray): Option[Hit] = {
    var sceneObjects = vector filter (_.isInstanceOf[SceneObject])
    sceneObjects.foldLeft(None: Option[Hit])({
      case (p, (SceneObject (g, m))) => {
        g.intersect(ray,range,m) match {
          case Some (nh) => Some(nh)
          case _ => p
        }
      }
    })
  }
  def intersect(ray:Ray): Option[Hit] = intersect(new Range (0.0, MAX_DOUBLE), ray)


}
