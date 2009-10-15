import javax.vecmath._
import java.io._

class Image ()
{
  var  width  = 300;
  var  height = 300;
  
  var image =  Array.ofDim [Point3i] (300,300);

  
  
  def setColor(x:Double ,  y:Double,  color:Color3f) {
    val myColor = new Point3i ((color.x * 255).toInt, 
			      (color.y * 255).toInt,(color.z * 255.0).toInt);

    image(x.toInt)(y.toInt) = myColor;
  }


  def writeImageStdOutput (): Unit =
    {
      System.out.println("P3");
      System.out.println("# Created by me");
      System.out.println(width + " " + height);
      System.out.println("255");
      var i =  height - 1    
      while (i >= 0)
      { 
        var j = width
        while (j < width)
        { 
          System.out.print(image (j)(i).x + " " + 
        		   image (j)(i).y + " " + 
         		   image (j)(i).z + " ");
          
          j = j + 1;
        }
        System.out.println();
        i = i -1
      }
      
    }

  def writeImage():Unit=
    {
      var printWriter: PrintWriter = null ;
      try {
        
        printWriter =  new PrintWriter(new BufferedWriter(new FileWriter(new File("image.ppm"))));

        

      } catch {
        case _ => {
                   println ("Errrorrr");
                   System.exit(1)
                 } //e.prIntStackTrace();
        
      }

      
        printWriter.println("P3");
        printWriter.println("# Created by me");
        printWriter.println(width + " " + height);
        printWriter.println("255");
        var i =  height - 1    
        while (i >= 0)
        { 
          var j = 0
          while (j < width)
          { 
            printWriter.print(image (j)(i).x + " " + 
        		      image (j)(i).y + " " + 
         		      image (j)(i).z + " ");
            
            j = j + 1;
          }
          printWriter.println();
          i = i -1
        }
      printWriter.close ();
    }
  
  
}

