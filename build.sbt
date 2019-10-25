import scala.sys.process.Process
import scala.util.Try

val gitHash = settingKey[String]("Git hash")
val dockerHttpPort = settingKey[Int]("HTTP listen port")
val defaultPort = 9000

val p = Project("ref-app", file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, DockerPlugin)
  .settings(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8"
    ),
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "5.1.47",
      "io.getquill" %% "quill-jdbc" % "3.4.10",
      "org.flywaydb" % "flyway-core" % "6.0.3",
      "com.zaxxer" % "HikariCP" % "3.3.1",
      "redis.clients" % "jedis" % "3.1.0",
      "com.lihaoyi" %% "scalatags" % "0.7.0",
      specs2 % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
      "org.seleniumhq.selenium" % "selenium-java" % "3.141.59" % Test
    ),
    dockerBaseImage := "openjdk:11",
    daemonUser in Docker := "daemon",
    dockerRepository := Option("malliina"),
    dockerExposedPorts := Seq(dockerHttpPort.value),
    javaOptions in Universal ++= Seq(
      "-J-Xmx256m",
      s"-Dhttp.port=${dockerHttpPort.value}"
    ),
    gitHash := Try(Process("git rev-parse --short HEAD").lineStream.head).toOption
      .orElse(sys.env.get("CODEBUILD_RESOLVED_SOURCE_VERSION").map(_.take(7)))
      .orElse(sys.env.get("CODEBUILD_SOURCE_VERSION").map(_.take(7)))
      .getOrElse("latest"),
    dockerHttpPort := sys.env.get("HTTP_PORT").map(_.toInt).getOrElse(defaultPort),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "gitHash" -> gitHash.value),
    buildInfoPackage := "com.malliina.refapp.build",
    pipelineStages := Seq(digest, gzip),
    unmanagedResourceDirectories in Assets += baseDirectory.value / "frontend" / "dist",
    PlayKeys.playRunHooks += new NPMRunHook(baseDirectory.value / "frontend", target.value, streams.value.log)
  )