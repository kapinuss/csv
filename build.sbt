name := "csv"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies ++= {
  val akkaV = "2.5.11"
  val akkaHttpV = "10.0.12"
  val phantomV = "2.14.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "0.18",
    "com.github.tototoshi" %% "scala-csv" % "1.3.5"
  )
}