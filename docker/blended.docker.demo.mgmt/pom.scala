import org.sonatype.maven.polyglot.scala.model._
import scala.collection.immutable.Seq

//#include ../../blended.build/build-versions.scala
//#include ../../blended.build/build-dependencies.scala
//#include ../../blended.build/build-plugins.scala
//#include ../../blended.build/build-common.scala

BlendedDockerContainer(
  gav = Blended.dockerDemoMgmt,
  image = Dependency(
    gav = Blended.demoMgmt,
    `type` = "tar.gz",
    classifier = "full-nojre",
    scope = "provided"
  ),
  folder = "mgmt",
  overlayDir = "${project.basedir}/../overlays",
  overlays = Seq("jvm-medium-1.conf"),
  ports = List(1099,1883,9191,8849,9995,9996)
)
