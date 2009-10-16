import java.awt.Color
import javax.vecmath._;
import scala.util.parsing.combinator.lexical._
import scala.util.parsing.combinator.syntactical._

/** 
 * ExprLexical
 * this is copied from this address. Basic support for floatTokens
 * http://jim-mcbeath.blogspot.com/2008/09/scala-parser-combinators.html  
 */ 
class ExprLexical extends StdLexical {
    override def token: Parser[Token] = floatingToken | super.token

    def floatingToken: Parser[Token] =
        rep1(digit) ~ optFraction ~ optExponent ^^
            { case intPart ~ frac ~ exp => NumericLit(
                    (intPart mkString "") :: frac :: exp :: Nil mkString "")}

    def chr(c:Char) = elem("", ch => ch==c )
    def sign = chr('+') | chr('-')
    def optSign = opt(sign) ^^ {
        case None => ""
        case Some(sign) => sign
    }
    def fraction = '.' ~ rep(digit) ^^ {
        case dot ~ ff => dot :: (ff mkString "") :: Nil mkString ""
    }
    def optFraction = opt(fraction) ^^ {
        case None => ""
        case Some(fraction) => fraction
    }
    def exponent = (chr('e') | chr('E')) ~ optSign ~ rep1(digit) ^^ {
        case e ~ optSign ~ exp => e :: optSign :: (exp mkString "") :: Nil mkString ""
    }
    def optExponent = opt(exponent) ^^ {
        case None => ""
        case Some(exponent) => exponent
    }
}

object SceneParser extends StandardTokenParsers {
  override val lexical = new ExprLexical
  lexical.delimiters ++= List("-","<",">",",","{","}")
  lexical.reserved ++= List( "background",
                             "color",
                             "camera",
                             "light_source",
                             "location",
                             "look_at",
			     "pigment",
                             "plane",
                             "sphere",
                             "Blue",
                             "Green",
                             "Red",
                             "Yellow",
                             "Black",
                             "White")

  // Doesn't accept decimals nor negatives
  def valueP = opt("-") ~ numericLit ^^ 
    {case Some(_) ~ s => -s.toDouble
     case None    ~ s   => s.toDouble}
  

  def backgroundP = "background" ~>"{" ~> colorP <~ "}" ^^
                     { case color => new Background (new Color3f (color)) }


  def colorP  = "color" ~> ("Blue" | "Green" | "Red" | "Yellow" | "White" | "Black") ^^
                       {case "Blue"   => Color.blue
		        case "Green"  => Color.green
		        case "Red"    => Color.red
		        case "Yellow" => Color.yellow
		        case "White"  => Color.white 
		        case "Black"  => Color.black
                      }
                 
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
    vectorValueP("sphere", {case (center,radius,pigment) => 
      new SceneObject (new Sphere(center, radius), new Material(pigment))})

  def planeP = 
    vectorValueP("plane", {case (center,radius,pigment) => 
      new SceneObject (new Plane(center, radius), new Material(pigment))})
  
  def cameraP = ("camera" ~> "{" ~> "location"~> vectorLitP ) ~
                ( "look_at" ~> vectorLitP <~ "}") ^^
                { case location ~ lookAt => new Camera (location,lookAt) }

  def lightP = ("light_source" ~> "{" ~> vectorLitP)~
               (colorP <~ "}")^^ 
               { case location ~ color => new LightSource (location,new Color3f (color)) }

  def sceneObjP = sphereP | planeP | cameraP | lightP | backgroundP

  // The scene is just a list of SceneObjects
  def sceneP: Parser[List[SceneElement]] = sceneObjP+

  def parse(s:String) = {
    val tokens = new lexical.Scanner(s)
    // Check there's only one camera and LightSource.
    //def checkTree (tree:List[SceneObject]) = (tree count (_.isInstanceOf[Camera])) >=0   &&
      //                                       (tree count (_.isInstanceOf[LightSource])) >= 0
    // lastFailure = None
    phrase(sceneP)(tokens) match {
      case Success(tree,_) => 
        if (true) { // checkTree(tree)
          Right(tree)
        }else{
          Left("Error in the number of cameras or light sources.")
        }
      case x => Left(x.toString)
    }
  }
}
