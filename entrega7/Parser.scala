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
  lexical.reserved ++= List( "Black",
                             "Blue",
                             "Green",
                             "Red",
                             "White",
                             "Yellow",
                             "ambient",
                             "ambient_light",
                             "background",
                             "camera",
                             "color",
                             "diffuse",
                             "finish",
                             "global_settings",
                             "kr", // FIXME
                             "light_source",
                             "location",
                             "look_at",
                             "phong",
                             "phong_size",
                             "pigment",
                             "plane",
                             "reflection",
                             "refraction_index", // FIXME
                             "sphere")

  // Doesn't accept decimals nor negatives
  def valueP = opt("-") ~ numericLit ^^
    {case Some(_) ~ s => -s.toDouble
     case None    ~ s   => s.toDouble}

  def finishValue =
    (("ambient"               |
      "diffuse"               |
      "refraction_index"      |
      "kr"                    |
      "phong"                 |
      "phong_size") ~ valueP) |
     ("reflection" ~ ("{" ~> valueP <~ "}"))

  def finishP = "finish" ~> "{" ~> finishValue <~ "}" ^^
  { case l ~ color => (l, color.toFloat) }

  def manyFinishP: Parser[List [(String,Float)]] = finishP*

  def backgroundP = "background" ~>"{" ~> colorP <~ "}" ^^
                     { case color => new Background (new Color3f (color)) }

  def colorP  = "color" ~> ("Blue" | "Green" | "Red" | "Yellow" | "White" | "Black") ^^
                       {case "Blue"   => new Color3f(Color.blue)
		        case "Green"  => new Color3f(Color.green)
		        case "Red"    => new Color3f(Color.red)
		        case "Yellow" => new Color3f(Color.yellow)
		        case "White"  => new Color3f(Color.white )
		        case "Black"  => new Color3f(Color.black)
                      }

  def pigmentP = "pigment" ~> "{" ~> colorP <~ "}"

  def vectorLitP = ("<" ~> valueP) ~
                   ("," ~> valueP) ~
                   ("," ~> valueP <~ ">") ^^
                   {case x ~ y ~ z => new Vector3d(x.toDouble,
                                                   y.toDouble,
                                                   z.toDouble)}

  // This one lifts a common constructor of objects like sphere and plane
  def sceneObjectP(cons: String, f: (Vector3d, 
                                     Double, 
                                     Material) => SceneObject) = {
    (cons ~> "{" ~> vectorLitP) ~
    ("," ~> valueP ) ~
    (pigmentP)~ (manyFinishP <~ "}") ^^ {
      case center ~ radius ~ pigment ~ ls => {
        def fst[A, B] (t:(A,B)): A = t match {case (a,b) => a}
        def snd[A, B] (t:(A,B)): B = t match {case (a,b) => b}

        def findReplace(s:String, d: Float): Float = {
          val opt = ls.find (((x:String) => s == x) compose (fst[String,Float]));
          return ((opt map (snd[String, Float])) getOrElse d);
        }
        val ka = findReplace("ambient"    , 0.1f)
        val kd = findReplace("diffuse"    , 0.7f)
        val ks = findReplace("phong"      , 0.7f)
        val kn = findReplace("phong_size" , 20.0f)
        val kr = findReplace("kr"         , 0f)
        val reflection = findReplace("reflection", 0.0f)
        val refraction = findReplace("refraction_index", 0.0f)
        val material = new Material(new Color3f (pigment),ka,kd,ks,kn,kr,reflection,refraction);
        f(center, radius, material)
      }
    }
  }

  def sphereP =
    sceneObjectP("sphere", {
      case (center, radius, material) => {
        new SceneObject (new Sphere(center, radius), material)
      }
    })

  def planeP =
    sceneObjectP("plane", {
      case (center, radius, material) => {
        new SceneObject (new Plane(center, radius), material)
      }
    })

  def cameraP = ("camera" ~> "{" ~> "location"~> vectorLitP ) ~
                ( "look_at" ~> vectorLitP <~ "}") ^^
                { case location ~ lookAt => new Camera (location,lookAt) }

  def lightP = ("light_source" ~> "{" ~> vectorLitP) ~ (colorP <~ "}") ^^
               { case location ~ color => new LightSource (location, color) }

  def ambientLightP = "ambient_light" ~>  colorP ^^ {
    case color => new AmbientLight (color)
  }

  def globalSettingsP = "global_settings" ~> "{" ~> ambientLightP <~ "}"
  // We could get more global settings so that we can do something similar
  // to what is done for sceneObjP

  def sceneObjP = planeP | sphereP | cameraP | lightP | backgroundP | globalSettingsP

  // The scene is just a list of SceneObjects
  def sceneP: Parser[List[SceneElement]] = sceneObjP+

  def parse(s:String) = {
    val tokens = new lexical.Scanner(s)
    // Check there's only one camera and LightSource.
    // def checkTree (tree:List[SceneObject]) = (tree count (_.isInstanceOf[LightSource]) || ) >= 0 
    // (tree count (_.isInstanceOf[Camera])) >=0  //  &&
    
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
