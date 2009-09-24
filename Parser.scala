import javax.vecmath._;
import scala.util.parsing.combinator.syntactical._
import java.awt.Color

object SceneParser extends StandardTokenParsers {

  lexical.delimiters ++= List("<",">",",","{","}")
  lexical.reserved ++= List( "background",
                             "Blue",
                             "color",
                             "camera",
                             "Green",
                             "light_source",
                             "location",
                             "look_at",
			     "pigment",
                             "plane",
                             "Red",
                             "sphere",
                             "Yellow",
                             "White")

  // Doesn't accept decimals nor negatives
  def valueP = numericLit ^^ (s => s.toDouble) 
  

  def backgroundP = "background" ~>"{" ~> colorP <~ "}" ^^
                     { case color => new Background (new Color3f (color)) }


  def colorP  = "color" ~>
                      ("Blue"|"Green"|"Red"|"Yellow"|"White") ^^ 
                     {  case "Blue"   => Color.blue
		        case "Green"  => Color.green
		        case "Red"    => Color.red
		        case "Yellow" => Color.yellow
		        case "White"  => Color.white }
                 
  def pigmentP = "pigment" ~> "{" ~> colorP <~ "}" 

  def vectorLitP = ("<" ~> valueP) ~ 
                   ("," ~> valueP) ~ 
                   ("," ~> valueP <~ ">") ^^ 
                   {case x ~ y ~ z => new Vector3d(x.toDouble, 
                                                   y.toDouble, 
                                                   z.toDouble)}

  


  // This one lifts a common constructor of objects like sphere and plane 
  def vectorValueP(cons: String, f: Function[(Vector3d, Double, Color3f), SceneObject]) = 
                 (cons ~> "{" ~> vectorLitP) ~ 
                  ("," ~> valueP ) ~
                   (pigmentP <~ "}") ^^
                  {case center ~ radius ~ pigment=> f(center, radius, new Color3f ( pigment ))}
  
  def sphereP = 
    vectorValueP("sphere", {case (center,radius,pigment) => new Sphere(center, radius, pigment)})

  def planeP = 
    vectorValueP("plane", {case (center,radius,pigment) => new Plane(center, radius, pigment)})
  
  def cameraP = ("camera" ~> "{" ~> "location"~> vectorLitP ) ~
                ( "look_at" ~> vectorLitP <~ "}") ^^
                { case location ~ lookAt => new Camera (location,lookAt) }

  def lightP = ("light_source" ~> "{" ~> vectorLitP)~
               (colorP <~ "}")^^ 
               { case location ~ color => new LightSource (location,new Color3f (color)) }

  def sceneObjP = sphereP | planeP | cameraP | lightP | backgroundP

  // The scene is just a list of SceneObjects
  def sceneP: Parser[List[SceneObject]] = rep(sceneObjP)

  def parse(s:String) = {
    val tokens = new lexical.Scanner(s)
    // Check there's only one camera and LightSource.
    def checkTree (tree:List[SceneObject]) = (tree count (_.isInstanceOf[Camera])) >=0   &&
                                                                    (tree count (_.isInstanceOf[LightSource])) >= 0
    // lastFailure = None
    phrase(sceneP)(tokens) match {
      case Success(tree,_) => 
        if (checkTree(tree)) {
          Right(tree)
        }else{
          Left("Error in the number of cameras or light sources.")
        }
      case x => Left(x.toString)
    }
  }
  
  
}


