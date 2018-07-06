package courier.client.io

import java.lang.SuppressWarnings
import java.nio.file.{ Path, Paths }
import scala.util.{ Failure, Success }
import utest._

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object FileReadTest extends TestSuite {
  val tests = Tests {
    lazy val path = "./client/src/test/resources/testDir/1.txt"

    "Read a simple file" - {
      val p: Path = Paths.get(path)

      FileRead.read(p) match {
        case Success(bytes) ⇒ {
          val res = new String(bytes)
          assert("Hello world!" == res.trim())
        }

        case Failure(e) ⇒ assert(false)
      }
    }
  }
}
