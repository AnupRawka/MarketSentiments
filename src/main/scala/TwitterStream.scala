import twitter4j._;
// reference: http://bcomposes.com/2013/02/09/using-twitter4j-with-scala-to-access-streaming-tweets/
object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setDebugEnabled(true)
    .setOAuthConsumerKey("zkjWmJoa1FCgtjbu2RXCJVhd6")
    .setOAuthConsumerSecret("EYO4pGLyNOMKfhhBmikYxhIB6wRIGfOkEefMUytHHBwzPQyooI")
    .setOAuthAccessToken("3022942130-Ab1uE72GEezuUwhUz2oMvRBErhoe1XxRv3yhw6k")
    .setOAuthAccessTokenSecret("E1lxRxiAD0mcnE7ckGe9ONZxgsxmLrnzJlerwgo4pQivP")

  val tf : TwitterFactory = new TwitterFactory(config.build());
  val twitter: Twitter = tf.getInstance()
  try {
    val query = new Query("Tendulkar")
    query.setLang("en")
    query.setCount(100)
    val result = twitter.search(query)
    println("Tweet :"+result.getTweets())
    System.exit(0)
  } catch {
    case te: TwitterException =>
      println("Failed to search tweets: " + te.getMessage)
  }
  //Below method outputs the text of the tweet status to twitter stream listener
}

object StatusStreamer {
  def main(args: Array[String]) {
Util
  }
}
