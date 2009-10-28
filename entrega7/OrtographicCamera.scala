import javax.vecmath._;

case class OrtographicCamera ( size:Double ) 
{
  def generateRay ( point:Point2d ):Ray = 
    {    
     
      var ray = new Ray ( new Vector3d(point.getX(),point.getY(),0d), new Vector3d(0.0,0.0,-1.0));
      return ray;
    }
  
}
