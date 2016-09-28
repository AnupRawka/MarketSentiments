package cassandra

import java.util.{Calendar, Properties}
import com.datastax.driver.core.Cluster
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import com.datastax.driver.core.exceptions._
import com.google.common.io.Resources
object CassandraController{
    val CasProps = Resources.getResource("cassandra.properties").openStream()
    val properties = new Properties()
    properties.load(CasProps)
    val hostIp = properties.getProperty("cassandra.host")
    val portnum =  properties.getProperty("cassandra.port")
    var cluster: Cluster= null
    def connect(hostIp: String, portnum: Int){
       cluster = Cluster.builder().addContactPoint(hostIp).withPort(portnum).build();
      val metadata = cluster.getMetadata()
      printf("Connected to cluster: %s\n", metadata.getClusterName())
      metadata.getAllHosts foreach {
        case host =>
          println("Datatacenter: %s; Host: %s; Rack: %s\n",
            host.getDatacenter(), host.getAddress(), host.getRack())
      }
    }
    def getSession(): Session ={
      var session: Session= null
      try {
        session = cluster.connect()
      }
      catch {
        case ex:NoHostAvailableException => println("No host available, unable to connect")
      }
      session
      }
//http://stackoverflow.com/questions/26546299/result-type-of-an-implicit-conversion-must-be-more-specific-than-anyref
  def insert(ticker :java.lang.String, score : java.lang.Integer): Unit = {
    try {
    val prepared = getSession().prepare(properties.getProperty("query.insert"))
    val timeStamp = Calendar.getInstance().getTime()
    val result = CassandraController.getSession().execute(prepared.bind(ticker, timeStamp, score))
    println("query execution result is : " + result)
  }
    catch {
      case syntax: SyntaxError => println("There is a syntax error in your query")
      case _: Throwable => println("Failed to execute query")
    }

  finally{
    cluster.close()
  }
  }

    def close() {
      cluster.close()
    }


  }

