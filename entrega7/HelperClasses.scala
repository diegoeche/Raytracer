import javax.vecmath._;

case class Ray   (var origin:Vector3d, var direction:Vector3d )
case class Range (var minT:Double,    var maxT:Double )
case class Hit   (var t:Double,
                  var normal: Vector3d,
                  var location: Vector3d,
                  var material: Material)
