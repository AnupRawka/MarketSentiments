package kafka

import java.io.{PrintWriter, StringWriter}
import java.util.Properties
import com.google.common.io.Resources
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
object ScalaProducer {

  def run(tweets: (String, String)) {
    //def run(tweets: (String, String)){
    var producer: KafkaProducer[String, String] = null
    try {
      val props = Resources.getResource("producer.props").openStream()
      val properties = new Properties()
      properties.load(props)
      producer = new KafkaProducer[String, String](properties)
      producer.send(new ProducerRecord[String, String]("TweetsQueue", tweets._1+"|"+tweets._2))
      producer.flush()
      println("Sent msg from producer")
      producer.close()
    }
    catch {
      case throwable: Throwable =>
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        println(sw.toString())
    }
    finally {
      producer.close()
    }
  }
}
