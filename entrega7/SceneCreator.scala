import javax.vecmath._;
import scala.Math._;

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
      var k = v1.dot(normal)
      k *= (-2.0)
      val dir = new Vector3d(normal)
      dir.scale(k)
      dir.add(v1)
      dir.normalize()
      return dir
    }

    // def calculateReflectedRay(i: Ray, pos: Point3d, normal: Vector3d) {

    // }
    // public Ray CalcularReflectedRay(Ray incidente, Point3d posicion, Vector3d normal){
    //   double k = incidente.getDireccion().dot(normal);
    //   k = k * (-2.0);
    //   Vector3d direccion = new Vector3d((k*normal.x)+incidente.getDireccion().x,
    //     				(k*normal.y)+incidente.getDireccion().y,
    //     				(k*normal.z)+incidente.getDireccion().z);
    //   Vector3d pos = new Vector3d(posicion.x,posicion.y,posicion.z);
    //   return new Ray(pos,direccion);

    // }
    def getLightDirection(pos: Vector3d, l: LightSource): Vector3d = {
      val direction = new Vector3d(pos)
      direction.sub(l.location)
      direction.normalize()
      return direction
    }

    def specularIllumination(r: Ray, m: Material, l: LightSource): Color3f = {
      val direction = getLightDirection (r.origin, l)
      val reflected = r.direction
      var specular  = reflected.dot(direction)
      specular = if (specular <= 0) 0 else specular
      specular = pow(specular , m.kn);
      specular = specular * m.ks
      val color     = ***(l.color, new Color3f(1,1,1))
      color.scale (specular.toFloat)
      return color
    }

    def diffuseIllumination(h: Hit, l: LightSource): Color3f = {
      val direction = getLightDirection(h.location, l)
      var diffuse = (new Vector3d(direction)).dot(h.normal).toFloat 
      diffuse = if (diffuse <= 0) 0 else diffuse
      val color = ***(l.color, h.material.pigment)
      color.scale(diffuse)
      color.scale(h.material.kd)
      return color
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
            val amb = ***(color, ambient)
            amb.scale(mat.ka)
            val reflectedRay = new Ray(hit.location, reflectedVector(ray.direction, hit.normal))
            val total = new Color3f()
            lights foreach ((l:LightSource) =>
              total.add(diffuseIllumination(hit, l))) // All diffuse
            lights foreach ((l:LightSource) =>
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
