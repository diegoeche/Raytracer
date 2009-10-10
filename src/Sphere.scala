import javax.vecmath._;

case class Sphere (center:Point3d, radius: Double, color:Color3f) extends Object3D
{ 
  def intersect (r:Ray, h:Hit, range:Range, tMin:TValue): Boolean=
    {
      // ToDo:
      // Compute the intersection of the ray with the
      // Sphere and update what needs to be updated
      // Valid values for t must be within the "range"
      return true;
    }
}
