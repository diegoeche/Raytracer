import javax.vecmath._;

case class Material(pigment: Color3f)

class SceneElement
class Geometry()

sealed case class Background  (color : Color3f)                        extends SceneElement
sealed case class Camera      (location: Vector3d, lookAt: Vector3d)   extends SceneElement
sealed case class LightSource (location: Vector3d, color: Color3f)     extends SceneElement
sealed case class SceneObject (geometry: Geometry, material: Material) extends SceneElement
sealed case class Sphere      (center:Vector3d   , radius: Double)     extends Geometry
sealed case class Plane	      (point:Vector3d	 , distance: Double) extends Geometry

