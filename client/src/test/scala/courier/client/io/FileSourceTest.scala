package courier.client.io

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import java.nio.file.Path
import org.scalatest.AsyncFlatSpec
import scala.concurrent.Future
import scala.util.{ Failure, Success }


/** Unit tests for FileSource */
class FileSourceTest extends AsyncFlatSpec {
  implicit lazy val system = ActorSystem("TestSystem")
  implicit lazy val materializer = ActorMaterializer()

  lazy val testPath = "./client/src/test/scala/courier/client/io"
  lazy val dfs = DefaultFileSource(testPath)

  "DefaultFileSource" should "find some number of files in" in {

    val res: Future[Int] =
      dfs.find()
        .map(_ => 1)
        .runWith(Sink.fold(0)(_ + _))

    res.map(count => assert(count > 0))
  }

  it should "Find this source file in the stream" in {
    val res: Future[Option[Path]] =
      dfs.find()
        .filter(p => p.getFileName.toString() == "FileSourceTest.scala")
        .runWith(Sink.headOption)
    res.map(opt => assert(opt.isDefined))
  }

  it should "find all 3 files without directories" in {
    val path = "./client/src/test/resources/testDir"
    val fs = DefaultFileSource(path, withDirs = false)
    fs.find()
      .limit(100)
      .map{ it => it.getFileName.toString() }
      .runWith(Sink.seq).map{ elems =>
        assert(elems.size == 3 && elems.contains("1.txt"))
      }
  }
}
