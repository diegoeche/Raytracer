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
                             "light_source",
                             "location",
                             "look_at",
                             "phong_size",
                             "phong",
                             "pigment",
                             "plane",
                             "sphere")

  // Doesn't accept decimals nor negatives
  def valueP = opt("-") ~ numericLit ^^ 
    {case Some(_) ~ s => -s.toDouble
     case None    ~ s   => s.toDouble}

  def finishP = "finish" ~> "{" ~> ("ambient"|"diffuse"|"phong"|"phong_size") ~ valueP <~ "}" ^^
                          {
                           case "ambient"    ~ color => ("ambient"    ,color)
                           case "diffuse"     ~ color => ("diffuse"   ,color)
                           case "phong"      ~ color => ("phong"      ,color)
                           case "phong_size" ~ color => ("phong_size" ,color)
                           case "size"       ~ color => ("size"       ,color)
                          }

  def manyFinishP: Parser[List [(String,Double)]] = finishP*
  

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
  def vectorValueP(cons: String, f: Function[(Vector3d, Double, Color3f,Double,Double,Double,Double), SceneObject]) = 
                 (cons ~> "{" ~> vectorLitP) ~ 
                  ("," ~> valueP ) ~
                   (pigmentP)~ (manyFinishP <~ "}") ^^
                   {
                    //case center ~ radius ~ pigment ~ List () => f(center, radius, new Color3f ( pigment ))
                    case center ~ radius ~ pigment ~ ls => 

                        val  ka = ls.find ( _ match {
                       
                                                  case ("ambient",_) => true
                                                  case  _            => false
                               }) match {
                                 case Some ((_,v)) => v
                                 case None         => 0.1
                               }

                       val  kd =  ls.find ( _ match {
                                                  case ("diffuse",_) => true
                                                  case  _            => false
                               }) match {
                                 case Some ((_,v)) => v
                                 case None         => 0.7
                               }
                      
                       val  ks =  ls.find ( _ match {
                                                  case ("phong",_) => true
                                                  case  _            => false
                               }) match {
                                 case Some ((_,v)) => v
                                 case None         => 0.7
                               }

                       val n = ls.find ( _ match {
                                                  case ("phong_size",_) => true
                                                  case  _            => false
                               }) match {
                                 case Some ((_,v)) => v
                                 case None         => 20.0
                               }
                     
                      
                      f(center, radius, new Color3f ( pigment),ka,kd,ks,n )
                   }
  
  def sphereP = 
    vectorValueP("sphere", {case (center,radius,pigment,ka,kd,ks,n) => 
      new SceneObject (new Sphere(center, radius), new Material(pigment,ka,kd,ks,n))})

//  def planeP = 
//  vectorValueP("plane", {case (center,radius,pigment,) => 
//     new SceneObject (new Plane(center, radius), new Material(pigment))})
  
  def cameraP = ("camera" ~> "{" ~> "location"~> vectorLitP ) ~
                ( "look_at" ~> vectorLitP <~ "}") ^^
                { case location ~ lookAt => new Camera (location,lookAt) }


  def lightP = ("light_source" ~> "{" ~> vectorLitP)~(colorP <~ "}")^^ 
               { case location ~ color => new LightSource (location,new Color3f (color)) }


  def ambientLightP = "ambient_light" ~>  colorP ^^ {
                                                    case color => new AmbientLight ( new Color3f (color)) 
                                                    }


  def globalSettingsP = "global_settings" ~> "{" ~> ambientLightP <~ "}" // We could get more global settings so that we can do something similar
                                                                         // to what is done for sceneObjP
                          

  def sceneObjP = sphereP |  cameraP | lightP | backgroundP | globalSettingsP

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
