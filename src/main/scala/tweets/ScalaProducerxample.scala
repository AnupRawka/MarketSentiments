package tweets

import java.io.{PrintWriter, StringWriter}
import java.util.Properties
import com.google.common.io.Resources
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.collection.mutable

object ScalaProducer {

  def run(tweets: (String, scala.collection.mutable.Buffer[String])){

    var producer: KafkaProducer[String, scala.collection.mutable.Buffer[String]] = null
    try {
      val props = Resources.getResource("producer.props").openStream()
      val properties = new Properties()
      properties.load(props)
      producer = new KafkaProducer[String, scala.collection.mutable.Buffer[String]](properties)
      println("Tweets are ----:"+tweets)
      producer.send(new ProducerRecord[String, scala.collection.mutable.Buffer[String]](tweets))
      producer.flush()
      println("Sent msg")
      producer.close()
    }
    catch {
      case throwable: Throwable =>
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        println(sw.toString())
    }
    //finally {
      //producer.close()
    //}
  }
}
//object ScalaProducer {
//  def main(args: Array[String]): Unit = {
//    val scalaProducer = new ScalaProducer()
//    //scalaProducer.run(args)
//  }
//}
