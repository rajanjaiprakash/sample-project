import sbtrelease._
import sbtrelease.ReleaseStateTransformations._

name := "hello-demo"
scalaVersion := "2.13.0"

releaseUseGlobalVersion := false
releaseIgnoreUntrackedFiles := true
fork in run := true

lazy val root = (project in file(".")).settings(resolvers += Resolver.sonatypeRepo("releases"))

enablePlugins(DockerPlugin, JavaServerAppPackaging)

daemonUserUid in Docker := None
daemonUser in Docker    := "daemon"
dockerBaseImage := "openjdk:8-jre-alpine"
dockerExposedPorts := Seq(9000)
dockerUsername := Some("dragonaire")
dockerRepository := Some("dragonaire/hello-demo")
dockerAlias := dockerAlias.value.withRegistryHost(None)

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
  setReleaseVersion,
//  commitReleaseVersion,
  tagRelease,
  releaseStepTask(publish in Docker),
  pushChanges
)


commands += Command.command("releaseBugfix")((state: State) => {
  println("Preparing bugfix...")
  val extracted = Project.extract(state)

  val st = extracted.appendWithSession(Seq(releaseVersion := { ver =>
    Version(ver).fold(versionFormatError(ver))(
      _.withoutQualifier.bumpBugfix.string)
  }), state)
  Command.process("release with-defaults", st)
})

commands += Command.command("releaseMinor")((state: State) => {
  println("Preparing minor release...")
  val extracted = Project.extract(state)

  val st = extracted.appendWithSession(Seq(releaseVersion := { ver =>
    Version(ver).fold(versionFormatError(ver))(
      _.withoutQualifier.bumpMinor.string)
  }), state)
  Command.process("release with-defaults", st)
})

commands += Command.command("releaseOverride")((state: State) => {
  println("Preparing to override release...")
  val extracted = Project.extract(state)

  val st = extracted.appendWithSession(Seq(releaseVersion := {ver =>
    Version(ver).fold(versionFormatError(ver))(
      _.withoutQualifier.bumpNano.string)}
  ), state)
  Command.process("release with-defaults default-tag-exists-answer o", st)
})

commands += Command.command("releaseMajor")((state: State) => {
  println("Preparing Major release...")
  val extracted = Project.extract(state)

  val st = extracted.appendWithSession(Seq(releaseVersion := { ver =>
    Version(ver).fold(versionFormatError(ver))(
      _.withoutQualifier.bumpMajor.string)
  }), state)
  Command.process("release with-defaults", st)
})