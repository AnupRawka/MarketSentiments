package kafka

import java.io.{PrintWriter, StringWriter}
import java.util._

import cassandra.CassandraController
import com.google.common.io.Resources
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}
//object kafka.ScalaConsumer {
//  def main(args: Array[String]): Unit = {
//    val scalaConsumer = new kafka.ScalaConsumer()
//    scalaConsumer.run(args)
//  }
//}
///**
//  * This program reads messages from two topics.
//  * Whenever a message is received on "slow-messages", the stats are dumped.
//  */
object ScalaConsumer {
  var boolean = true
  var consumer: KafkaConsumer[String, String] = null
  val kafkaprops = Resources.getResource("consumer.props").openStream()
  val CasProps = Resources.getResource("cassandra.properties").openStream()
  val properties = new Properties()
  properties.load(CasProps)
  val hostIp = properties.getProperty("cassandra.host")
  val portnum  =  properties.getProperty("cassandra.port")
  CassandraController.connect(hostIp, portnum.toInt)
  properties.load(kafkaprops)
  if (properties.getProperty("group.id") == null) {
    properties.setProperty("group.id", "TweetsQueue")
  }
  consumer = new KafkaConsumer[String, String](properties)
  consumer.subscribe(Arrays.asList("TweetsQueue"))
  def run {
    try {
      while (boolean == true) {
        println("consumer loop running, wait for messages")
        val records: ConsumerRecords[String, String] = consumer.poll(200)
        val recordCount = records.count()
        println(s"Got $recordCount records")
        val iter = records.iterator()
        while (iter.hasNext()) {
          val record: ConsumerRecord[String, String] = iter.next()
          val values=record.value().split(("""\|"""))
          val timeStamp = Calendar.getInstance().getTime()
          CassandraController.insert(values(0),values(1).toInt)
          CassandraController.close()
          boolean = false
        }
        boolean=false
      }
    }
    catch {
      case throwable: Throwable =>
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        println(sw.toString())
    }
    finally {
      consumer.close()
      CassandraController.close()
      println("Consumer and cassandra connection is closed")
    }
  }
}