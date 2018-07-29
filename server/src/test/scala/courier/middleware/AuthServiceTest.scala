package courier.middleware

import courier.auth.{ AuthToken, ClientId, InMemoryAuthStore }
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.util.CaseInsensitiveString
import cats.effect.IO
import utest._
import courier.auth._
import io.circe.generic.auto._
import io.circe.syntax._

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
object AuthServiceTest extends TestSuite {
  val tests = Tests {
    val idString = "cliente"
    val tokenString = "deadbeef"
    val sampleCreds = Credenciales(ClientId(idString), AuthToken(tokenString))

    // Preparar el authstore a usar para la autenticacion
    implicit val store =
      InMemoryAuthStore().store(ClientId(idString), AuthToken(tokenString))

    // El servicio con el que se hacen las pruebas
    val service = AuthService {
      case GET -> Root / "test" =>
        Ok("test")
    }

    "authFailNoCreds" - {
      val request = mkRequest(None)
      val response = runRequest(request, service)
      assert(response.status == Status.Forbidden)
    }

    "authFailWrongHeaders" - {
      val request = mkRequest(Some(("Whatever", "Whatever")))
      val response = runRequest(request, service)
      assert(response.status == Status.Forbidden)
    }

    'authSuccessGoodCreds - {
      val request = mkRequest(Some(AuthService.expectedHeader -> sampleCreds.asJson.toString))
      val response = runRequest(request, service)
      assert(response.status == Status.Ok)
      val text = response.as[String].unsafeRunSync
      assert(text == "test")
    }
  }

  // Helper to build a request with optional headers
  private[this] def mkRequest(header: Option[(String, String)]): Request[IO] = {
    val hs = header.map { case (key, value) => Headers(Header(key, value)) }
      .getOrElse(Headers())
    Request[IO](Method.GET, uri("/test")).withHeaders(hs)
  }

  // Helper to run a request on a service
  private[this] def runRequest(request: Request[IO], service: HttpService[IO]): Response[IO] =
    service.orNotFound.run(request).unsafeRunSync()
}
