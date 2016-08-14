import java.util.Properties

import com.datastax.driver.core.Cluster
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import com.google.common.io.Resources

object cassandraConnector{
  val CasProps = Resources.getResource("cassandra.properties").openStream()
  val properties = new Properties()
  properties.load(CasProps)
  val hostIp = properties.getProperty("cassandra.host")
  val portnum =  properties.getProperty("cassandra.port")
    var cluster: Cluster = null
    private var session: Session = null
    def connect(hostIp: String, portnum: Int) {
      cluster = Cluster.builder().addContactPoint(hostIp).withPort(portnum).build();
      val metadata = cluster.getMetadata()
      printf("Connected to cluster: %s\n", metadata.getClusterName())
      metadata.getAllHosts() map {
        case host =>
          printf("Datatacenter: %s; Host: %s; Rack: %s\n",
            host.getDatacenter(), host.getAddress(), host.getRack())
      }
      session=cluster.connect()
    }
    def getSession(): Session ={
      session
    }

    def close() {
      cluster.close()
    }
  }
//object cassandraRunner {
//  def main(args: Array[String]) {
//    cassandraConnector.connect("127.0.0.1",9042)
//    val createMovieCql =
//      "CREATE TABLE tweet_sentiments.movies (title varchar, year int, description varchar, mmpa_rating varchar, dustin_rating varchar, PRIMARY KEY (title, year))"
//    cassandraConnector.getSession().execute(createMovieCql)
//    cassandraConnector.close()
//  }
//}


