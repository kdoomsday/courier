package courier

import cats.effect._
import courier.auth._
import fs2.StreamApp.ExitCode
import fs2.{ Stream, StreamApp }
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext.Implicits.global
import io.circe._, io.circe.generic.auto._, io.circe.syntax._, io.circe.parser._
import org.http4s.circe._
import courier.auth.DummyAutenticador
import middleware.LoggingService

object CourierServer extends StreamApp[IO] {
  val autenticador = DummyAutenticador
  val store = InMemoryAuthStore()

  // Servicios

  val helloService: HttpService[IO] = LoggingService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")
  }

  def shutdownService(requestShutdown: IO[Unit]) = LoggingService.wrap(HttpService[IO] {
    case GET -> Root / "shutdown" =>
      requestShutdown.unsafeRunSync()
      Ok()
  })

  /** Servicio para recibir las credenciales y contestar con el token de
    * autenticación
    */
  val authService = LoggingService[IO] {
    case req @ POST -> Root / "authenticate" =>
      req.decode[UrlForm] { data: UrlForm =>
        val as: Either[String, String] =
          data
            .values
            .get("auth")
            .getOrElse(Nil)
            .headOption
            .toRight("No credentials")

        // format: OFF
        val resp = for {
          authString <- as
          creds      <- parseCredenciales(authString)
          authInfo   <- autenticador.autenticar(creds)
          _           = storeInfo(creds, authInfo)
          tokenJson   = authInfo.token.asJson
        } yield Ok(tokenJson)
        // format: ON

        resp.getOrElse(NotFound())
      }
  }

  /** Convertir de json a Credenciales */
  private[this] def parseCredenciales(jsonString: String): Either[Error, ClientId] =
    decode[ClientId](jsonString)

  /** Almacenar la información de autenticación en el store */
  private[this] def storeInfo(id: ClientId, ai: AuthInfo): Unit = {
    val _: InMemoryAuthStore = store.store(id, ai.token)
    ()
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(9000, "localhost")
      .mountService(helloService)
      .mountService(shutdownService(requestShutdown))
      .mountService(authService)
      .serve
  }
}
