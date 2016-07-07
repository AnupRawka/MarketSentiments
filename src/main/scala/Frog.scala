/**
  * Created by Astha on 6/27/2016.
  */
class point (val x : Int, val y:Int)
class rectangular(val topLeft: point, val bottomRight: point){
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
}

abstract class component(){
  def topLeft: point
  def bottomRight: point
  def left=topLeft.x
  def right = bottomRight.x
  def width=left-right
}

trait Rectangular{
    def topLeft: point
    def bottomRight: point
    def left=topLeft.x
    def right = bottomRight.x
    def width=left-right
}

object main{
  def main (arg : Array [String]){
    val lst = List(1,2,3,4)
    println(lst.tail)
  }
}