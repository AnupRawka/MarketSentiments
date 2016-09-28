package quotes
import java.util.{Calendar, Date}
object QuotesManager {

//  def fetchLatestQuotes(tickerID: String, score: Int, timeStamp: Date): Seq[RealTimeQuote] = {
def fetchLatestQuotes()  : Seq[RealTimeQuote] = {
  val tweetSentimentsMap= tweets.Util.getTweets()
  tweets.Util.sendTweets()
  tweetSentimentsMap.map {
    case (tickerId: String, score: Float) =>
      println("*******************Fetching Latest Quotes********************")
      val timeStamp = Calendar.getInstance().getTime()
      new RealTimeQuote(tickerId, timeStamp, (10.5).toFloat,(10.5).toFloat, score)
  }.toSeq
}

}
case class HistoricalQuote(id: String, dateTime: Date, openPrice: Float, highOfTheDay: Float, lowOfTheDay: Float, closePrice: Float, sentimentScore: Float)

case class RealTimeQuote(id: String, dateTime: Date, openPrice: Float, currentPrice: Float, var sentimentScore: Float)