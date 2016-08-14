package tweets

import java.util.{Date, Properties}

import com.google.common.io.Resources
import com.typesafe.config.ConfigFactory
import kafka.{ScalaConsumer, ScalaProducer}
import org.apache.kafka.clients.producer.KafkaProducer
import sentiment._
import twitter4j._
import utils.ApplicationConstants

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.language.postfixOps
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
  ///val tempKeys = List("Infosys", "Bloomberg", "Amazon", "JPM Chase", "Facebook")
  //val tempKeys =List("Yahoo")
  //val sinceIDmap: scala.collection.mutable.Map[String, Long] = collection.mutable.Map(tempKeys map { ix => s"$ix" -> 0.toLong }: _*)
  //val myCfg =  ConfigFactory.parseFile(new File("MarketSentimentAnalysis/src/main/conf/application.conf"))
  val configBuild = (config.build())
  val MAX_TWEET = 100
  SentimentAnalyzer.init
  val sinceIdMap: scala.collection.mutable.Map[String, Long] = mutable.Map() ++ ConfigFactory.load().getConfigList(ApplicationConstants.TICKERS).map {
    config =>
      (config.getString(ApplicationConstants.ID), 0.toLong)
    //println("ID is : "+config.getString(ApplicationConstants.ID))
  }.toMap

  val tickerQueryMap = ConfigFactory.load().getConfigList(ApplicationConstants.TICKERS).map {
    config =>
      (config.getString(ApplicationConstants.ID), config.getString("twitterQuery"))
  } toMap

def getTweetSentiment(): Map[String, (String,scala.collection.mutable.Buffer[String])] = {
    tickerQueryMap.keys.map {
      tickerId => {
        println("tickerQueryMap(tickerId))  :" + tickerQueryMap(tickerId))
        (tickerId, getTweetStream(tickerId))
      }
    }.toMap
  }

  getTweetSentiment()

  def getTweets(): Unit = {
    sinceIdMap.keys.foreach {
      TickerId =>
        //---comented on 07/31 ---- var producer: KafkaProducer[String, mutable.Buffer[String]] = null
        var producer: KafkaProducer[String, String] = null
        val props = Resources.getResource("producer.props").openStream()
        val properties = new Properties()
        properties.load(props)
        ScalaProducer.run(getTweetStream(TickerId))
        println("Sent msg to consummer")
        ScalaConsumer.run
        println("cursor back from consumer")
    }
  }
  //---Commented on 07/31
  def getTweetStream(TickerId: String): (String,scala.collection.mutable.Buffer[String]) = {
    //def getTweetStream(TickerId: String): (String, String) = {
    println("Search key is:" + TickerId)
    //var tweets = (String, scala.collection.mutable.Buffer[String])
    val query = new Query(tickerQueryMap(TickerId))
    val twitter: Twitter = new TwitterFactory(configBuild).getInstance
    println("we are here"+query)
    val QResult = twitter.search(query)

    try {
      query.setSinceId(sinceIdMap.get(TickerId).get)
      query.setCount(MAX_TWEET)
      query.setLang("en")

      //tweets += (TickerId -> QResult.getTweets.map(_.getText))
      //println("***************++++++++++++++*******"+TickerId -> QResult.getTweets.map(_.getText))
      //println("-----------Since id is :" + QResult.getTweets.max.getId)
      val result = QResult.getTweets.max.getId
      if (result.equals(null))
      sinceIdMap(TickerId) = QResult.getTweets.max.getId
      else
        sinceIdMap(TickerId) = 0.toLong

    }
    catch {
      case te: TwitterException =>
        println("Failed to search tweets: " + te.getMessage)
    }
    val sentimentScoreList = QResult.getTweets.map(getTweetSentimentScore(_, TickerId))
    //var tweetSentiment = new TweetSentiment(new DateTickerCompositeKey(new Date(System.currentTimeMillis()), TickerId), 0, 0)
    println(" sentiment score is :" + sentimentScoreList)
    (TickerId -> QResult.getTweets.map(_.getText))
  }

  private def getTweetSentimentScore(tweet: Status, tickerId: String): Int = {
    //println("Tweet is :"+tweet.getText)
    (SentimentAnalyzer.findSentiment(tweet.getText) * 25).toInt
  }
}

object StatusStreamer {
  def main(args: Array[String]) {
    Util
  }
}
