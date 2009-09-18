import javax.vecmath._;

sealed class SceneObject
sealed case class Sphere(center:Vector3d, 
                         radius: Double)  extends SceneObject
sealed case class Plane (point:Vector3d, 
                         distance: Double) extends SceneObject
sealed case class LightSource (location: Vector3d,
			       color: Color3f) extends SceneObject
sealed case class Camera (location: Vector3d,
			  lookAt: Vector3d) extends SceneObject 

