package courier

import cats.effect.IO
import org.http4s.{ Method, Request, Response }
import org.http4s.dsl.io._
import utest.TestSuite
import utest._

/** Tests de CourierServer */
object TestCourierServer extends TestSuite {
  val tests = Tests {
    "hello service responds with the name" - {

      @SuppressWarnings(Array("org.wartremover.warts.Throw"))
      val req = Request[IO](Method.GET, uri("/hello/Eduardo"))

      val response: Response[IO] =
        CourierServer.helloService.orNotFound.run(req).unsafeRunSync()

      assert(response.status.code == 200)

      val body = new String(response.body.compile.toList.unsafeRunSync().toArray)
      assert(body endsWith "Eduardo!")
    }
  }
}
