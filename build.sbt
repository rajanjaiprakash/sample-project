
name := "hello-demo"
version := "0.1"
scalaVersion := "2.13.0"

releaseUseGlobalVersion := false

lazy val root = (project in file(".")).settings(resolvers += Resolver.sonatypeRepo("releases"))

enablePlugins(GitVersioning, DockerPlugin)

git.useGitDescribe := true

publishTo := Some("sample-projects" at "https://github.com/rajanjaiprakash")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")