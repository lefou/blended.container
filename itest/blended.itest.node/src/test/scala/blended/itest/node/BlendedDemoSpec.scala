package blended.itest.node

import java.io.File

import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

import akka.actor.ActorRef
import akka.camel.CamelExtension
import akka.testkit.TestKit
import akka.util.Timeout
import blended.itestsupport.{BlendedIntegrationTestSupport, ContainerSpecSupport}
import blended.testsupport.camel._
import blended.util.FileHelper
import org.scalatest.{ DoNotDiscover, FreeSpec, Matchers }

@DoNotDiscover
class BlendedDemoSpec(ctProxy: ActorRef)(implicit testKit : TestKit) extends FreeSpec
  with Matchers
  with BlendedIntegrationTestSupport
  with ContainerSpecSupport
  with CamelTestSupport {

  implicit val system = testKit.system
  implicit val timeOut = Timeout(30.seconds)
  implicit val eCtxt = testKit.system.dispatcher
  override implicit val camelContext = CamelExtension.get(system).context

  private[this] val log = testKit.system.log

  "The demo container should" - {

    "Define the sample Camel Route from SampleIn to SampleOut" in {

      val testMessage = createMessage(
        message = "Hello Blended!",
        properties = Map("foo" -> "bar"),
        evaluateXML = false,
        binary = false
      )

      val outcome = Map(
        "jms:queue:SampleOut" -> Seq(
          ExpectedMessageCount(1),
          ExpectedBodies("Hello Blended!"),
          ExpectedHeaders(Map("foo" -> "bar"))
        )
      )

      blackboxTest(
        message = testMessage,
        entry = "jms:queue:SampleIn",
        outcome = outcome,
        testCooldown = 5.seconds
      ) should be (empty)
    }

    "Allow to read and write directories via the docker API" in {

      import blended.testsupport.BlendedTestSupport.projectTestOutput

      val file = new File(s"${projectTestOutput}/data")

      writeContainerDirectory(ctProxy, "node_0", "/opt/node", file).onComplete {
        case Failure(t) => fail(t.getMessage())
        case Success(r) => r.result match {
          case Left(t) => fail(t.getMessage())
          case Right(f) =>
            if (!f._2) fail("Error writing container directory")
            else {
              readContainerDirectory(ctProxy, "node_0", "/opt/node/data") onComplete {
                case Failure(t) => fail(t.getMessage())
                case Success(cdr) => cdr.result match {
                  case Left(t) => fail(t.getMessage())
                  case Right(cd) =>
                    cd.content.get("data/testFile.txt") match {
                      case None => fail("expected file [/opt/node/data/testFile.txt] not found in container")
                      case Some(c) =>
                        val fContent = FileHelper.readFile("data/testFile.txt")
                        c should equal(fContent)
                    }
                }
              }
            }
        }
      }
    }

    "Allow to execute an arbitrary command on the container" in {

      execContainerCommand(
        ctProxy = ctProxy,
        ctName = "node_0",
        cmdTimeout = 5.seconds,
        user = "blended",
        cmd = "ls -al /opt/node".split(" "): _*
      ) onComplete {
        case Failure(t) => fail(t.getMessage())
        case Success(r) =>
          r.result match {
            case Left(t) => fail(t.getMessage())
            case Right(er) =>
              log.info(s"Command output is [\n${new String(er._2.out)}\n]")
              er._2.rc should be(0)
          }
      }
    }
  }
}