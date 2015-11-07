lazy val root = (project in file(".")).
  settings(
    name := "scindex",
    version := "1.0",
    scalaVersion := "2.11.7",
    resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
  ).settings(libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
  "org.rocksdb" % "rocksdbjni" % "3.9.0", //wait for 3.11.1 fix bug
  "com.google.guava" % "guava" % "18.0",
  "com.twitter" %% "util-collection" % "6.23.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "log4j" % "log4j" % "1.2.17",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
))
