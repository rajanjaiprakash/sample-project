import sbtrelease._
import sbtrelease.ReleaseStateTransformations._


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
dockerUsername := Some("dragonaire")
dockerRepository := Some("https://cloud.docker.com/repository/registry-1.docker.io/dragonaire/sample-proj")

def setVersion(selectVersion: Versions => String): ReleaseStep = { st: State =>

  val vs = st.get(ReleaseKeys.versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
  val selected = selectVersion(vs)

  st.log.info("Setting version to '%s'." format selected)
  val useGlobal = Project.extract(st).get(releaseUseGlobalVersion)

  reapply(Seq(
    if (useGlobal) version in ThisBuild := selected
    else version := selected
  ), st)
}

lazy val setGitReleaseVersion: ReleaseStep = setVersion(_._1)

releaseProcess := Seq(
  releaseStepCommand(ExtraReleaseCommands.initialVcsChecksCommand),
  checkSnapshotDependencies,
  inquireVersions,
  setGitReleaseVersion,
  runClean,
  runTest,
  tagRelease,
  releaseStepTask(publish in Docker),
  pushChanges
)
