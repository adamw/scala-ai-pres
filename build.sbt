ThisBuild / scalaVersion := "3.9.0"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.ai" %% "openai" % "0.11.0",
  "com.softwaremill.chimp" %% "chimp-server-ox" % "0.5.2",
  "org.virtuslab" %% "orca" % "0.1.9"
)
