package courier

import cats.effect._
import courier.auth.Credenciales
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
        data.values.get("auth") match {
          case Some(authinfo) =>
            val creds: Either[Error, Credenciales] = parseCredenciales(authinfo.headOption.getOrElse(""))
            mkCredsResponse(creds)

          case None =>
            NotFound()
        }
      }
  }

  val simpleAuthService = HttpService[IO] {
    case GET -> Root / "authenticate" / id =>
      mkCredsResponse(Right[Error, Credenciales](Credenciales(id)))
  }

  /** Convertir de json a Credenciales */
  private[this] def parseCredenciales(jsonString: String): Either[Error, Credenciales] =
    decode[Credenciales](jsonString)

  /** Dadas las credenciales, construir la respuesta */
  private[this] def mkCredsResponse(eCreds: Either[Error, Credenciales]): IO[Response[IO]] = {
    eCreds match {
      case Left(_) => NotFound()
      case Right(creds) =>
        autenticador.autenticar(creds)
          .fold(_ => NotFound(), token => Ok(token.asJson))
    }
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(9000, "localhost")
      .mountService(helloService)
      .mountService(shutdownService(requestShutdown))
      .mountService(authService)
      .mountService(simpleAuthService)
      .serve
  }
}
