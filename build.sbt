
name := "hello-demo"
version := "0.1"
scalaVersion := "2.13.0"

releaseUseGlobalVersion := false

lazy val root = (project in file(".")).settings(resolvers += Resolver.sonatypeRepo("releases"))

enablePlugins(GitVersioning)

git.useGitDescribe := true