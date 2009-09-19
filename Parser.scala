import javax.vecmath._;
import scala.util.parsing.combinator.syntactical._
import java.awt.Color

object SceneParser extends StandardTokenParsers {

  lexical.delimiters ++= List("<",">",",","{","}")
  lexical.reserved ++= List( "Blue",
                             "color",
                             "camera",
                             "Green",
                             "light_source",
                             "location",
                             "look_at",
                             "plane",
                             "Red",
                             "sphere",
                             "Yellow",
                             "White")

  def valueP = numericLit ^^ (s => s.toDouble) 
  
  def colorP  = ("Blue"|"Green"|"Red"|"Yellow"|"White") ^^ 
  { case "Blue"   => Color.blue
    case "Green"  => Color.green
    case "Red"    => Color.red
    case "Yellow" => Color.yellow
    case "White"  => Color.white }
                 

  def vectorLitP = ("<" ~> valueP) ~ 
                   ("," ~> valueP) ~ 
                   ("," ~> valueP <~ ">") ^^ 
                   {case x ~ y ~ z => new Vector3d(x.toDouble, 
                                                   y.toDouble, 
                                                   z.toDouble)}

  // This one lifts a common constructor of objects like sphere and plane
  def vectorValueP(cons: String, f: Function[(Vector3d, Double), SceneObject]) = 
                  (cons ~> "{" ~> vectorLitP) ~ 
                  ("," ~> valueP <~ "}") ^^
                  {case center ~ radius => f(center, radius)}
  
  def sphereP = 
    vectorValueP("sphere", {case (center,radius) => 
                               new Sphere(center, radius)})

  def planeP = vectorValueP("plane", 
                             {case (center,radius) => 
                               new Plane(center, radius)})
  
  def cameraP = ("camera" ~> "{" ~> "location"~> vectorLitP ) ~
                ( "look_at" ~> vectorLitP <~ "}") ^^
                { case location ~ lookAt => new Camera (location,lookAt) }

  def lightP = ("light_source" ~> "{" ~> vectorLitP)~
               ("color"~>colorP <~ "}")^^ 
               { case location ~ color => new LightSource (location,new Color3f (color)) }


  def parse(s:String) = {
    val tokens = new lexical.Scanner(s)
    phrase(planeP)(tokens) match {
      case Success(tree,_) => tree
      case _ => null
    }
  }
}
