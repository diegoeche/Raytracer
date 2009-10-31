import javax.vecmath._;
import scala.Math._;
import NiceVector._;

object Main extends Application {
  def process (l:List[SceneElement]):Unit = {
    val size = 300;
    var image:Image = new Image (size,size)
    var camera = new OrtographicCamera (1d);
    var group = new Group ( l );

    def getBlackAsDefaultBackground() =
      l.find(_.isInstanceOf[Background]) map (_.asInstanceOf[Background].color) getOrElse (new Color3f(0,0,0))
    def getBlackAsDefaultAmbient() =
      l.find(_.isInstanceOf[AmbientLight]) map (_.asInstanceOf[AmbientLight].color) getOrElse (new Color3f(0,0,0))

    // def getBlackAsDefault[A](f: A => Color3f) =
    //   (l.find(_.isInstanceOf[A])) map (f compose ((x:Object) => x.asInstanceOf[A])) getOrElse (new Color3f(0,0,0))

    val ambient    = getBlackAsDefaultAmbient()
    val background = getBlackAsDefaultBackground()
    val lights     = l.filter(_.isInstanceOf[LightSource]).asInstanceOf[List[LightSource]]

    def *** (v1: Tuple3f, v2: Tuple3f) =
      new Color3f(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z)

    def reflectedVector(v1: Vector3d, normal: Vector3d):Vector3d = {
      var k = (v1 * normal) * -2.0
      (((normal:NiceVector) scale k) + v1).normalize.v
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

    def calculateColor (ray: Ray, depth: Int): Color3f = {
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
              val reflectedColor = calculateColor(reflectedRay, depth - 1) // ***(calculateColor(reflectedRay, depth - 1), mat.pigment)
              reflectedColor.scale(mat.reflection)
              total.add(reflectedColor)
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
        image.setColor(x, y, calculateColor(ray, 8))
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
