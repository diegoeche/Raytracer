import javax.vecmath._;
import scala.util.parsing.combinator.syntactical._
//import AST._;

object SceneParser extends StandardTokenParsers {
  lexical.reserved   ++= List("plane", "sphere")
  lexical.delimiters ++= List("<",">",",","{","}")

  def valueP = numericLit ^^ (s => s.toDouble) 

  def vectorLitP = ("<" ~> valueP) ~ 
                   ("," ~> valueP) ~ 
                   ("," ~> valueP <~ ">") ^^ 
                   {case x ~ y ~ z => new Vector3d(x.toDouble, y.toDouble, z.toDouble)}

  // This one lifts a common constructor of objects like sphere and plane
  def vectorValueP(cons: String, f: Function[(Vector3d, Double), Any]) = 
                  (cons ~> "{" ~> vectorLitP) ~ 
                  ("," ~> valueP <~ "}") ^^
                  {case center ~ radius => f(center, radius)}
  
  def sphereP = vectorValueP("sphere", 
                             {case (center,radius) => 
                               new Sphere(center, radius)})

  def planeP = vectorValueP("plane", 
                             {case (center,radius) => 
                               new Plane(center, radius)})

  def parse(s:String) = {
    val tokens = new lexical.Scanner(s)
    phrase(planeP)(tokens) match {
      case Success(tree,_) => tree
      case _ => null
    }
  }
}
