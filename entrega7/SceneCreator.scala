import javax.vecmath._;
import scala.Math._;
import NiceVector._;

object Main extends Application {
  def process (l:List[SceneElement]):Unit = {
    val size = 600;
    var image:Image = new Image (size,size)
    var camera = new OrtographicCamera (1d);
    var group = new Group ( l );
    var dflag = 1
    def getBlackAsDefaultBackground() =
      l.find(_.isInstanceOf[Background]) map (_.asInstanceOf[Background].color) getOrElse (new Color3f(0,0,0))
    def getBlackAsDefaultAmbient() =
      l.find(_.isInstanceOf[AmbientLight]) map (_.asInstanceOf[AmbientLight].color) getOrElse (new Color3f(0,0,0))

    val ambient    = getBlackAsDefaultAmbient()
    val background = getBlackAsDefaultBackground()
    val lights     = l.filter(_.isInstanceOf[LightSource]).asInstanceOf[List[LightSource]]

    def reflectedVector(v1: Vector3d, normal: Vector3d):Vector3d = {
      var k = (v1 * normal) * -2.0
      (((normal:NiceVector) scale k) + v1).normalize.v
    }

    def refractedVector(v1: Vector3d, normal: Vector3d, n1: Double, n2: Double): Vector3d = {
      val n = n1 / n2
      val c1 = -(normal * v1)
      val c2 = Math.sqrt(1 - (n * n) * (1 - (c1 * c1)))
      val normalNV:NiceVector = normal
      val v1NV:NiceVector = v1
      val rr:NiceVector = (v1NV scale n) + (normalNV scale (n * c1 - c2)) 
      return rr.normalize.v
    }

    def getLightDirection(pos: Vector3d, l: LightSource): Vector3d =
      return (l.location - pos).normalize.v

    def specularIllumination(r: Ray, m: Material, l: LightSource): Color3f = {
      val direction = getLightDirection (r.origin, l)
      val reflected = r.direction
      var specular  = reflected.dot(direction)
      specular = if (specular <= 0) 0 else specular
      specular = pow(specular , m.kn);
      specular = specular * m.ks
      return (l.color *** (1,1,1)) scale specular
    }

    def diffuseIllumination(h: Hit, l: LightSource): Color3f = {
      val direction:NiceVector = getLightDirection(h.location, l)
      var diffuse = (direction * h.normal).toFloat
      diffuse = if (diffuse <= 0) 0 else diffuse
      return (l.color *** h.material.pigment) scale (diffuse * h.material.kd).toDouble
    }

    def calculateColor (ray: Ray, depth: Int, currentN: Option[Double]): Color3f = {
      if (depth == 0) {
        return new Color3f(0,0,0)
      }else{
        var hitP = group.intersect(ray)
        hitP match {
          case Some(hit) => {
            val mat = hit.material
            val color = mat.pigment
            val amb = (color *** ambient) scale mat.ka
            val reflectedRay = new Ray(hit.location, reflectedVector(ray.direction, hit.normal))
            val total = new Color3f()
            def isVisible (l: LightSource): Boolean = {
              val ray = new Ray(hit.location, getLightDirection(hit.location, l))
              val relativePosition: NiceVector = (l.location - hit.location)
              val maxT = Math.sqrt (relativePosition * relativePosition)
              return group.intersect(new Range(0, maxT), ray).isEmpty
            }
            val visibleLigths = lights filter isVisible

            visibleLigths foreach ((l:LightSource) =>
              total.add(diffuseIllumination(hit, l))) // All diffuse
            visibleLigths foreach ((l:LightSource) =>
              total.add(specularIllumination(reflectedRay, mat, l))) // All specular
            if(mat.reflection > 0) {
              val reflectedColor:NiceVector = calculateColor(reflectedRay, depth - 1, None)
              total.add(reflectedColor scale mat.reflection)
            }
            if(mat.kr > 0) {
              val (n1,n2) = currentN match {
                case None   => (1., mat.refraction)
                case Some(n) => (n, 1.)
              }
              // println(n1,n2)
              val refractedV = refractedVector(ray.direction, hit.normal, n1, n2)
              val refractedRay = new Ray(hit.location, refractedV)
              val refractedColor:NiceVector =
                if (currentN.isEmpty) {
                  calculateColor(refractedRay, depth - 1, Some(n2))
                } else {
                  calculateColor(refractedRay, depth - 1, None)
                }

              total.add(refractedColor scale mat.kr)
            }
            total.add(amb)
            total.clamp(0,1)
            return total
          }
          case None => background
        }
      }
    }
    for (x <- 0 until size ) {
      var px = (x.toDouble/(size/2.0)) - 1.0
      for (y <-0 until size) {
        var py = (y.toDouble/(size/2.0)) - 1.0;
        var ray = camera.generateRay(new Point2d (px,py));
        image.setColor(x, y, calculateColor(ray, 8, None))
      }
    }
    image.writeImage ();
  }

  def parseScene() = {
    val text = io.Source.fromPath("scene.pov").mkString
    SceneParser.parse(text) match {
      case Left(err)   => {println(err); exit(0);}
      case Right(tree) => process(tree);
    }
  }
  parseScene ();
}
