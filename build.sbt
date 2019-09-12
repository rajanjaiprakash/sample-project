
name := "hello-demo"
version := "0.1"
scalaVersion := "2.13.0"

releaseUseGlobalVersion := false

lazy val root = (project in file(".")).settings(resolvers += Resolver.sonatypeRepo("releases"))

enablePlugins(GitVersioning, DockerPlugin)

git.useGitDescribe := true

daemonUserUid in Docker := None
daemonUser in Docker    := "daemon"
dockerBaseImage := "openjdk:8-jre-alpine"
dockerExposedPorts := Seq(9000)
dockerUsername := Some("rajanjaiprakash")
dockerRepository := Some("https://cloud.docker.com/repository/registry-1.docker.io/dragonaire/sample-proj")