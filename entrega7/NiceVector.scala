import javax.vecmath._;

class NiceVector(val v: Vector3d) {
  def + (v2: NiceVector): NiceVector = {
    val result = (new Vector3d(v));
    result.add(v2.v)
    return new NiceVector(result);
  }
  def - (v2: NiceVector): NiceVector = {
    val result = (new Vector3d(v));
    result.sub(v2.v)
    return new NiceVector(result);
  }
  def scale (s: Double): NiceVector = {
    val result = (new Vector3d(v));
    result.scale(s)
    return new NiceVector(result);
  }
  def normalize: NiceVector = {
    val result = (new Vector3d(v));
    result.normalize()
    return new NiceVector(result);
  }
  def * (v2: NiceVector): Double = {
    v.dot(v2.v)
  }
  def ***(v2: NiceVector): NiceVector = {
    return new NiceVector(new Vector3d(v.x * v2.v.x, v.y * v2.v.y, v.z * v2.v.z));
  }
  def square = this *** this

  override def toString() = v.toString()
}

object NiceVector {
  implicit def vector3dToNiceVector(v:Vector3d) = new NiceVector(v)
  implicit def color3fToNiceVector(v:Color3f) = new NiceVector(new Vector3d(v.x,v.y,v.z))
  implicit def tupleToNiceVector(t:(Double, Double, Double)) = 
     t match {case (x,y,z) => new NiceVector(new Vector3d(x,y,z))}
  implicit def niceVectorToColor3f(v:NiceVector) = 
    new Color3f(v.v.x.toFloat, v.v.y.toFloat, v.v.z.toFloat)
}

import NiceVector._;
