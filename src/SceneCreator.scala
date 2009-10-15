import javax.vecmath.Point2d;

object Main extends Application {
  
  
  def process (l:List[SceneElement]):Unit =
    {
      val size = 300;  
      var image:Image = new Image ()
      var camera = new OrtographicCamera (1d);
      var group = new Group ( l );
      for (x <- 0 until size )
        {
          var px = (x.toDouble/(size/2.0)) - 1.0
          for (y <-0 until size)
            {
              var py = (y.toDouble/(size/2.0)) - 1.0;
              var ray = camera.generateRay( new Point2d (px,py));
                          
              var h =group.intersect (ray).color
              image.setColor ( x,y, h);
            }
          
        }
      println ("Writing")
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
