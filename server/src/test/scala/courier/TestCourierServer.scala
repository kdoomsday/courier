package courier

import cats.effect.IO
import courier.auth.{ AuthToken, ClientId }
import io.circe._, io.circe.generic.auto._, io.circe.syntax._
import courier.auth.Credenciales
import org.http4s.UrlForm
import org.http4s.{ Method, Request, Response, UrlForm }
import org.http4s.dsl.io._
// import org.http4s.client.dsl._
import org.http4s.circe.jsonOf
import utest.TestSuite
import utest._

/** Tests de CourierServer */
object TestCourierServer extends TestSuite {
  val tests = Tests {
    "hello service responds with the name" - {

      @SuppressWarnings(Array("org.wartremover.warts.Throw"))
      val req = Request[IO](Method.GET, uri("/hello/Eduardo"))

      val response: Response[IO] =
        (new CourierServer()).helloService.orNotFound.run(req).unsafeRunSync()

      assert(response.status.code == 200)

      val body = new String(response.body.compile.toList.unsafeRunSync().toArray)
      assert(body endsWith "Eduardo!")
    }

    "AuthService" - {
      "good request" - {
        @SuppressWarnings(Array("org.wartremover.warts.Throw"))
        val response =
          (new CourierServer())
            .authService
            .orNotFound
            .run(testRequest())
            .unsafeRunSync()

        assert(response.status.code == 200)
      }

      "updates store after auth" - {
        val server = new CourierServer()
        @SuppressWarnings(Array("org.wartremover.warts.Throw"))
        val response: Response[IO] =
          server.authService
            .orNotFound
            .run(testRequest())
            .unsafeRunSync()

        assert(response.status.code == 200)

        implicit val decoder = jsonOf[IO, AuthToken]
        val token = response.as[AuthToken].unsafeRunSync

        assert(token.id != "")
      }
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private[this] def testRequest(): Request[IO] =
    Request[IO](Method.POST,
                uri("/authenticate"))
      .withBody(UrlForm("auth" -> ClientId("eduardo").asJson.toString()))
      .unsafeRunSync()
}
