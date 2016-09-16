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
  def run {
    var boolean = true
    var consumer: KafkaConsumer[String, String] = null
    val kafkaprops = Resources.getResource("consumer.props").openStream()
    val CasProps = Resources.getResource("cassandra.properties").openStream()
    val properties = new Properties()
    properties.load(CasProps)
    val hostIp = properties.getProperty("cassandra.host")
    val portnum  =  properties.getProperty("cassandra.port")
    CassandraController.connect(hostIp, portnum.toInt)
    try {
      //val properties = new Properties()
      properties.load(kafkaprops)
      if (properties.getProperty("group.id") == null) {
        properties.setProperty("group.id", "test")
        //println("here")
      }
      consumer = new KafkaConsumer[String, String](properties)
      consumer.subscribe(Arrays.asList("test"))
      //var tweetSeq = new ListBuffer[String, Date, Float, Float, Float]
      while (boolean == true) {
        println("consumer loop running, wait for messages")
        // read records with a short timeout. If we time out, we don't really care.
        val records: ConsumerRecords[String, String] = consumer.poll(200)
        val recordCount = records.count()
        println(s"Got $recordCount records")
        val iter = records.iterator()
        while (iter.hasNext()) {
          val record: ConsumerRecord[String, String] = iter.next()
          println("*************** Record  is : "+record.value())
          val values=record.value().split(("""\|"""))
          println("consumer received message is : " + record.key()+ "  :  "+values(0)+"----"+values(1).toInt)
          val timeStamp = Calendar.getInstance().getTime()
          CassandraController.insert(values(0),values(1).toInt)
          //QuotesManager.fetchLatestQuotes(values(0),values(1).toInt,timeStamp)
          //tweetSeq+=(values(0),timeStamp, values(1).toFloat, 10.5, 10.5)
          //CassandraController.close()

          //println("***************************"+record.value())
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