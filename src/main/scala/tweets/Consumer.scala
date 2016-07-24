package tweets

import java.util.{Arrays, Properties, Random}

import com.google.common.io.Resources
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

//object tweets.ScalaConsumer {
//  def main(args: Array[String]): Unit = {
//    val scalaConsumer = new tweets.ScalaConsumer()
//    scalaConsumer.run(args)
//  }
//}
///**
//  * This program reads messages from two topics.
//  * Whenever a message is received on "slow-messages", the stats are dumped.
//  */
class ScalaConsumer {
  def run{
    var consumer : KafkaConsumer[String, String] = null
    try {
      val props = Resources.getResource("consumer.props").openStream()
      val properties = new Properties()
      properties.load(props)
      if (properties.getProperty("group.id") == null) {
        properties.setProperty("group.id", "group-" + new Random().nextInt(100000))
        println("here")
      }
      consumer = new KafkaConsumer[String, String](properties)
      consumer.subscribe(Arrays.asList("test"))
      var timeouts = 0

      while (true) {
        //println("consumer loop running, wait for messages")
        // read records with a short timeout. If we time out, we don't really care.
        val records : ConsumerRecords[String, String] = consumer.poll(200)
        val recordCount = records.count()
        if (recordCount == 0) {
          timeouts = timeouts + 1
        } else {
          println(s"Got $recordCount records after $timeouts timeouts\n")
          timeouts = 0
        }
        val iter   = records.iterator()
        while(iter.hasNext()) {
          val record : ConsumerRecord[String,String] = iter.next()
          println(record)
          println(record.value())
        }
      }
    }
    catch {
      case throwable : Throwable =>
        val st = throwable.getStackTrace()
        println(s"Got exception : $st")
    }
    finally {
      consumer.close()
    }
  }
}