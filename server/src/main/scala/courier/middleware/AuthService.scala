package courier.middleware

import cats.data.{ Kleisli, OptionT }
import cats.effect.IO
import courier.auth.{ AuthStore, Credenciales }
import org.http4s.util.CaseInsensitiveString
import org.http4s._
import org.http4s.dsl.io._
import io.circe.generic.auto._
import io.circe.parser.decode

object AuthService {
  val expectedHeader = "X-Auth-Token"
  // El tipo de los servicios que usa http4s
  type PFService = PartialFunction[Request[IO], IO[Response[IO]]]

  // Envolver un servicio normal en un middleware de autenticador
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def wrap(service: HttpService[IO])(implicit auth: AuthStore[_]): HttpService[IO] =
    Kleisli { req: Request[IO] =>
      val checked: OptionT[IO, Response[IO]] =
        getCreds(req).flatMap { creds =>
          if (auth.validate(creds))
            service(req)
          else
            OptionT.liftF(Forbidden())
        }

      checked.orElse(OptionT.liftF(Forbidden()))
    }

  // Conveniencia para definir los servicios autenticados
  def apply(pf: PFService)(implicit authStore: AuthStore[_]): HttpService[IO] =
    wrap(HttpService[IO](pf))

  /** @return Las credenciales en el header, si las hay y son válidas */
  private[this] def getCreds(req: Request[IO]): OptionT[IO, Credenciales] = {
    val headerValue: Option[String] =
      req.headers
        .get(CaseInsensitiveString(expectedHeader))
        .map(_.value)

    OptionT(IO(headerValue.flatMap(header => decode[Credenciales](header).toOption)))
  }

}
