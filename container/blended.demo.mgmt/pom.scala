import org.sonatype.maven.polyglot.scala.model._
import scala.collection.immutable.Seq

//#include ../../blended.build/build-versions.scala
//#include ../../blended.build/build-dependencies.scala
//#include ../../blended.build/build-plugins.scala
//#include ../../blended.build/build-common.scala

BlendedContainer(
  gav = Blended.demoMgmt,
  description = "A sample management container for the blended launcher.",
  blendedProfileResouces = Blended.demoMgmtResources,
  features = Seq(
    Feature("blended-base-felix"),
    Feature("blended-base-equinox"),
    Feature("blended-base"),
    Feature("blended-commons"),
    Feature("blended-jetty"),
    Feature("blended-security"),
    Feature("blended-hawtio"),
    Feature("blended-spring"),
    Feature("blended-activemq"),
    Feature("blended-camel"),
    Feature("blended-samples"),
    Feature("blended-mgmt-client"),
    Feature("blended-mgmt-server"),
    Feature("blended-akka-http"),
    Feature("blended-akka-http-modules"),
    Feature("blended-ssl")
  )
)
