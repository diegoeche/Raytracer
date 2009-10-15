import javax.vecmath._;

case class OrtographicCamera ( size:Double ) 
{
  def generateRay ( point:Point2d ):Ray = 
    {    
     
      var ray = new Ray ( new Point3d(point.getX(),point.getY(),0), new Vector3d(0d,0d,1d));
      return ray;
    }
  
}
