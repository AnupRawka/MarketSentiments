package tweets
import java.io.{PrintWriter, StringWriter}
import java.util.Properties
import com.google.common.io.Resources
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import twitter4j._
import scala.collection.JavaConversions._
import scala.collection.mutable
// reference: http://bcomposes.com/2013/02/09/using-twitter4j-with-scala-to-access-streaming-tweets/

object Util {
  val props = Resources.getResource("twitter4j.props").openStream()
  val properties = new Properties()
  properties.load(props)
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setDebugEnabled(properties.getProperty("debug").toBoolean)
    .setOAuthConsumerKey(properties.getProperty("consumerKey"))
    .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
    .setOAuthAccessToken(properties.getProperty("accessToken"))
    .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"))
  val tempKeys = List("Kabali", "Bloomberg", "Messi", "JPM Chase", "Facebook")
  //val tempKeys =List("Yahoo")
  val sinceIDmap: scala.collection.mutable.Map[String, Long] = collection.mutable.Map(tempKeys map { ix => s"$ix" -> 154.toLong }: _*)
  //val tweetsMap: scala.collection.mutable.Map[String, String]
  val configBuild = (config.build())
  val MAX_TWEET = 100
  getTweets()

  def getTweets(): Unit = {
    //sinceIDmap.keys.foreach((TickerId) => getTweets(TickerId))
    //sinceIDmap.keys.foreach((TickerId) => getTweets(TickerId))
    //sinceIDmap.keys.foreach((TickerId) => getTweets(TickerId))
    sinceIDmap.keys.foreach {
      TickerId =>
        //(getTweets(TickerId)){
        var producer: KafkaProducer[String, mutable.Buffer[String]] = null
        val props = Resources.getResource("producer.props").openStream()
        val properties = new Properties()
        properties.load(props)
        //producer = new KafkaProducer[String, mutable.Buffer[String]](properties)
        //producer.send(new ProducerRecord[String, mutable.Buffer[String]]("test",getTweetStream(TickerId)._2))
        //producer.send(new ProducerRecord[String, String]("test","message"))
        //println("-------:" + sinceIDmap.keys.foreach((TickerId) => getTweets(TickerId)))
        //println("-------Kabali :" +  getTweets("Kabali"))
        //producer.flush()
        ScalaProducer.run(getTweetStream(TickerId))
        println("Sent msg")
        val cons = new ScalaConsumer()
        cons.run

    }
    //val tweetMap: scala.collection.mutable.Map[String, scala.collection.mutable.Buffer[String]]= getTweets("Kabali")
    //println(" Here -------- :"+tweetMap)

    //tweetMap.keys.map {
      //tickerId => {
        //println(" here --- :" + tickerId + "   ##########################" + tweetMap.get(tickerId))

        //val tweet: (String, scala.collection.mutable.Buffer[String]) = (tickerId, tweetMap(tickerId))
        // ScalaProducer.run(tickerId, tweetMap(tickerId))
        //ScalaProducer.run(tweet)
        //val cons = new ScalaConsumer()
        //cons.run
      //}
    //}
  }

  def getTweetStream(TickerId: String): (String, scala.collection.mutable.Buffer[String]) = {
    println("Search key is:" + TickerId)
    //var tweets = (String, scala.collection.mutable.Buffer[String])
    val query = new Query(TickerId)
    val twitter: Twitter = new TwitterFactory(configBuild).getInstance
    val QResult = twitter.search(query)
    try {
      query.setLang("en")
      query.setSinceId(sinceIDmap.get(TickerId).get)
      query.setCount(MAX_TWEET)
      //tweets += (TickerId -> QResult.getTweets.map(_.getText))
      println("**********************"+TickerId -> QResult.getTweets.map(_.getText))
      println("-----------Since id is :" + QResult.getSinceId)
      //sinceIDmap(TickerId) = QResult.getSinceId
      //sinceIDmap(TickerId)=100.toLong     //println(tweets)
    }
    catch {
      case te: TwitterException =>
        println("Failed to search tweets: " + te.getMessage)
    }
    //println(" tweets are :" + tweets)
    (TickerId -> QResult.getTweets.map(_.getText))

  }

  //    try {
  //      //println("Tweet :"+result.getTweets().map(_.getText))
  //      var producer: KafkaProducer[String, String] = null
  //      try {
  //        val props = Resources.getResource("producer.props").openStream()
  //        val properties = new Properties()
  //        properties.load(props)
  //        producer = new KafkaProducer[String, String](properties)
  //        producer.send(new ProducerRecord[String, String]("test", "Tweet :" + result.getTweets().map(_.getText)))
  //        producer.flush()
  //        println("Sent msg")
  //        val cons = new ScalaConsumer()
  //        cons.run
  //      }
  //      catch {
  //        case throwable: Throwable =>
  //          val sw = new StringWriter();
  //          val pw = new PrintWriter(sw);
  //          throwable.printStackTrace(pw);
  //          println(sw.toString())
  //      }
  //      finally producer.close()
  //
  //      //val strProducer = Producer[String](topicName)
  //      System.exit(0)
  //    }
  ////
  //  catch {
  //    case te: TwitterException =>
  //      println("Failed to search tweets: " + te.getMessage)
  //  }
  //Below method outputs the text of the tweet status to twitter stream listener
}

object StatusStreamer {
  def main(args: Array[String]) {
Util
  }
}
