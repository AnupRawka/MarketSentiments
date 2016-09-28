package tweets

import java.text.SimpleDateFormat
import java.util.Properties

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
    val configBuild = (config.build())
    val MAX_TWEET = ApplicationConstants.MAX_COUNT
    val format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT)
    SentimentAnalyzer.init
    val sinceIdMap: scala.collection.mutable.Map[String, Long] = mutable.Map() ++ ConfigFactory.load().getConfigList(ApplicationConstants.TICKERS).map {
      config =>
        (config.getString(ApplicationConstants.ID), 0.toLong)
    }.toMap

    val tickerQueryMap = ConfigFactory.load().getConfigList(ApplicationConstants.TICKERS).map {
      config =>
        (config.getString(ApplicationConstants.ID), config.getString("twitterQuery"))
    } toMap

  def getTweets(): scala.collection.mutable.Map[String,Float] = {
  val tweetMap = collection.mutable.Map.empty[String,Float]
    sinceIdMap.keys.foreach {
      TickerId =>
        val tweet = getTweetStream(TickerId)
        tweetMap += (tweet._1 -> tweet._2.toFloat)
    }
  tweetMap
  }

  def sendTweets() {
    sinceIdMap.keys.foreach {
      TickerId =>
        var producer: KafkaProducer[String, String] = null
        val props = Resources.getResource("producer.props").openStream()
        val properties = new Properties()
        properties.load(props)
        val tweet = getTweetStream(TickerId)
        ScalaProducer.run(tweet)
        println("Sent msg to consummer")
        ScalaConsumer.run
        println("cursor back from consumer")
    }
  }
  def getTweetStream(TickerId: String): (String,String) = {
    val query = new Query(tickerQueryMap(TickerId))
    val twitter: Twitter = new TwitterFactory(configBuild).getInstance
    val QResult = twitter.search(query)

    try {
      query.setSinceId(sinceIdMap.get(TickerId).get)
      query.setCount(MAX_TWEET)
      query.setLang("en")
      if (QResult.getTweets.isEmpty) sinceIdMap(TickerId)
      else sinceIdMap(TickerId) = QResult.getTweets.max.getId-1
      }
    catch {
      case te: TwitterException =>
        println("Failed to search tweets: " + te.getMessage)
    }
    val sentimentScoreList = QResult.getTweets.map(getTweetSentimentScore(_, TickerId))
    val score = sentimentScoreList.foldLeft(0.0)((res, ts) => res + ts).toInt/sentimentScoreList.length
    (TickerId, score.toString)
  }

  private def getTweetSentimentScore(tweet: Status, tickerId: String): Int = {
    (SentimentAnalyzer.findSentiment(tweet.getText) * 25).toInt
  }
}

