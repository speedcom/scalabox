lazy val root = (project in file(".")).settings(
 name := "scalabox",
 version := "0.0.1",
 scalaVersion := "2.11.7",
 libraryDependencies ++= Seq(
    "org.scala-stm" %% "scala-stm" % "0.7",
     "commons-io" % "commons-io" % "2.4"
 )
)

