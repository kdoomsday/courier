package courier.client.io

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import java.nio.file.Path
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utest._

/** Unit tests de FileSource */
object FileSourceTest extends TestSuite {
  lazy val testPath = "./client/src/test/scala/courier/client/io"
  lazy val dfs = DefaultFileSource(testPath)

  implicit lazy val system = ActorSystem("TestSystem")
  implicit lazy val materializer = ActorMaterializer()

  val tests = Tests {
    "Find files" - {
      val res: Future[Int] =
        dfs.find()
          .map(_ => 1) // Cada archivo pone un 1
          .runWith(Sink.fold(0)(_ + _)) // Sumar los 1 me da la cuenta

      res.map(count => assert(count > 0))
    }

    "Find this file in the stream" - {
      val res: Future[Option[Path]] =
        dfs.find()
          .filter(p => p.getFileName.toString() == "FileSourceTest.scala")
          .runWith(Sink.headOption)
      res.map(opt => assert(opt.isDefined))
    }

    "Find all 3 files and no directories" - {
      val path = "./client/src/test/resources/testDir"
      val fs = DefaultFileSource(path, withDirs = false)
      fs.find()
        .limit(100)
        .map { it => it.getFileName.toString() }
        .runWith(Sink.seq).map { elems =>
          assert(elems.size == 3 && elems.contains("1.txt"))
        }
    }
  }
}
