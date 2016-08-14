package kafka

import java.io.{PrintWriter, StringWriter}
import java.util.{Arrays, Properties, Random}
import com.google.common.io.Resources
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}
import sentiment.SentimentAnalyzer

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
    val props = Resources.getResource("consumer.props").openStream()
    try {
      val properties = new Properties()
      properties.load(props)
      if (properties.getProperty("group.id") == null) {
        properties.setProperty("group.id", "group" + new Random().nextInt(100000))
        //println("here")
      }
      consumer = new KafkaConsumer[String, String](properties)
      consumer.subscribe(Arrays.asList("test"))
      //      var timeouts = 0

      while (boolean == true) {
        println("consumer loop running, wait for messages")
        // read records with a short timeout. If we time out, we don't really care.
        val records: ConsumerRecords[String, String] = consumer.poll(200)
        val recordCount = records.count()
        //        if (recordCount == 0) {
        //          timeouts = timeouts + 1
        //        } else {
        //          println(s"Got $recordCount records after $timeouts timeouts\n")
        //          timeouts = 0
        //        }
        val iter = records.iterator()
        while (iter.hasNext()) {
          val record: ConsumerRecord[String, String] = iter.next()
          //println("*************** Record  is : "+record.value())
          println("Sentiment score is : " + SentimentAnalyzer.findSentiment(record.value()))
          //println("***************************"+record.value())
          boolean = false
        }

      }
      consumer.close()
    }
    catch {
      case throwable: Throwable =>
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        println(sw.toString())
    }
    finally {
      //consumer.close()
      println("Consumer is closed")
    }
  }
}