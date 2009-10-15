import java.util.Vector;
import javax.vecmath.Color3f;

case class Group (var vector:List [SceneElement] )
{

      
    
      def intersect( ray:Ray ):Hit =
        {
          var hit     = new Hit (-1,new Color3f ());
          var range   = new Range ();
          println ("aqui");
          vector.foreach {
            case  SceneObject (g,Material (pigment)) =>
              g match {
                case s:Sphere  => {
                  s.intersect(ray,hit,range,pigment) match{
                    case Some ((nh,nr)) =>
                      {
                        hit   = nh;
                        range = nr;
                        
                      }
                  }
               }
              }
          }
          
            // ToDo:
           // Iterate over the array of objects,
         	// finding the intersections
	
         	// ...
         	return hit;
         }
   	

}
