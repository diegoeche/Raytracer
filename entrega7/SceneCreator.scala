import javax.vecmath._;

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

    def diffuseIllumination(h: Hit, l: LightSource): Color3f = {
      val direction = new Vector3d(h.location)
      direction.sub(l.location)
      direction.normalize()
      var diffuse = (new Vector3d(direction)).dot(h.normal).toFloat 
      diffuse = if (diffuse <= 0) 0 else diffuse
      val color = ***(l.color, h.material.pigment)
      println(diffuse)
      color.scale(diffuse)
      color.scale(h.material.kd)
      return color
    }


    def calculateColor (h: Hit): Color3f = {
      val mat = h.material
      val color = mat.pigment
      val amb = ***(color, ambient)
      amb.scale(mat.ka)
      val total = new Color3f()
      lights foreach ((l:LightSource) => total.add(diffuseIllumination(h, l))) // All diffuse
      total.add(amb) // Ambient
      total.clamp(0,1)
      return total;
    }

    for (x <- 0 until size ) {
      var px = (x.toDouble/(size/2.0)) - 1.0
      for (y <-0 until size) {
        var py = (y.toDouble/(size/2.0)) - 1.0;
        var ray = camera.generateRay(new Point2d (px,py));
        var h = (group.intersect(ray))
        h match {
          case Some(c) => image.setColor(x, y, calculateColor(c));
          case None    => image.setColor(x, y, background)
        }
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
