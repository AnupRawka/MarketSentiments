name := "MarketSentimentAnalysis"

version := "1.0"

scalaVersion := "2.11.8"
//libraryDependencies += groupID % artifactID % version % configuration

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.apache.kafka" % "kafka_2.10" % "0.8.0"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("org.slf4j", "slf4j-simple"),
  "org.apache.kafka" % "kafka-clients" % "0.9.0.0",
  "com.typesafe.play" % "play-json_2.11" % "2.4.6",
  //"org.slf4j" % "slf4j-simple" % "1.7.12",
  "com.google.guava" % "guava" % "19.0",
  "io.reactivex" % "rxscala_2.11" % "0.26.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1" classifier "models",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.0",
  "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.1.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.0-M1"
    exclude("io.netty", "netty*") exclude("org.apache.avro", "avro-ipc")
    exclude("org.apache.spark", "spark-core")
    exclude("org.apache.spark", "spark-sql")
    exclude("org.apache.cassandra.deps", "avro")
    exclude("commons-logging", "commons-logging")

)
scalacOptions ++= Seq("-feature")
unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"