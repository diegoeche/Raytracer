import java.util.Vector;

case class Group (vector : List [Object3D]) extends Object3D
{
      def add ( o3D:Object3D ):Unit = vector ::: List (o3D)
       
	
      def intersect( r:Ray, h:Hit, range:Range,tMin:TValue ):Boolean =
        {
		// ToDo:
		// Iterate over the array of objects,
		// finding the intersections
		val retVal = false;
		// ...
		return retVal;
   	}

}
