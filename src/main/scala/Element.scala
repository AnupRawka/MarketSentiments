/**
  * Created by Anup on 6/26/2016.
  */
abstract class Element {
def contents: Array[String]
  def height : Int = contents.length
  def width : Int = if (height == 0) 0 else contents(0).length
}

class ArrayElement (conts: Array [String]) extends Element {
  //def contents: Array[String]=conts
  val contents : Array[String]=conts
}

class UniformElement (
  ch: Char,
  override val height:Int,
  override val width: Int
  ) extends Element{
  private val line=ch.toString * width
  println("Line is:"+line)
  def contents=Array.fill(height)(line)
}
object test {
  def main (arg : Array [String]){
  val ae = new ArrayElement (Array("Anup Rawka","Astha"))
    val e2: Element = ae
    val e3: Element = new UniformElement('x', 2, 5)
    println(ae.height)
    println(ae.width)
    println()
  }
}