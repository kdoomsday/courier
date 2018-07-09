package courier

import cats.effect.IO
import io.circe._, io.circe.generic.auto._, io.circe.syntax._
import courier.auth.Credenciales
import org.http4s.UrlForm
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.{ Method, Request, Response, UrlForm }
import org.http4s.dsl.io._
// import org.http4s.client.dsl._
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

    "AuthService" - {
      "good request" - {
        val creds = Credenciales("eduardo")

        @SuppressWarnings(Array("org.wartremover.warts.Throw"))
        val req: Request[IO] = Request[IO](Method.POST,
                                           uri("/authenticate"))
          .withBody(UrlForm("auth" -> creds.asJson.toString()))
          .unsafeRunSync()

        val response = CourierServer.authService.orNotFound.run(req).unsafeRunSync()

        // TODO Extraer el json del body y sacar la informacion
        println(new String(response.body.compile.toList.unsafeRunSync().toArray))
        assert(response.status.code == 200)
      }
    }
  }
}
