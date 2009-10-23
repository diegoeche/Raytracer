import javax.vecmath._;


object Main extends Application {


  def process (l:List[SceneElement]):Unit = {
    val size = 300;
    var image:Image = new Image (size,size)
    var camera = new OrtographicCamera (1d);
    var group = new Group ( l );
    val Some(ambient) = (l.find(_.isInstanceOf[AmbientLight])) map (_.asInstanceOf[AmbientLight].color)
    val background = 
      (l.find(_.isInstanceOf[Background])) map (_.asInstanceOf[Background].color) getOrElse (new Color3f(0,0,0))
    val lights = l.filter(_.isInstanceOf[LightSource])

    def *** (v1: Tuple3f, v2: Tuple3f) =
      new Color3f(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z)

    def calculateColor (h: Hit): Color3f = {
      val color = h.material.pigment
      return (***(color,ambient));
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
