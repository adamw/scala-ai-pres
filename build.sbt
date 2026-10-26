resolvers += "scala-nightlies" at "https://repo.scala-lang.org/artifactory/maven-nightlies/"

ThisBuild / scalaVersion := "3.10.1-RC1-bin-20260929-af35eef-NIGHTLY"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.ai" %% "openai" % "0.11.0",
  "com.softwaremill.chimp" %% "chimp-server-ox" % "0.5.2",
  "org.virtuslab" %% "orca" % "0.2.0"
)
