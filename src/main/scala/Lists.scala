/**
  * Created by Astha on 6/28/2016.
  */
object runMain {
  def main(arg: Array[String]) {
    val sr = new sort
    println(sr.InsertionSort(Array(2,3,1, 4, 5, 6, 3, 8, 329)).deep.mkString("\n"))
  }
}
class sort{
  def InsertionSort(arr: Array[Int]): Array[Int] = {
    for (i <- 1 until arr.length) {
      var j = i - 1
      var tmp = arr(i)
      while (j > -1 && arr(j) > tmp) {
        arr(j+1) = arr(j)
        j -= 1
      }
      arr(j+1) = tmp
    }
    arr
  }


}