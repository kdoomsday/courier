package courier.middleware

import cats.Applicative
import org.http4s.{ HttpService, Request, Response }

object LoggingService {

  /** Servicio que hace log del llamado */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def wrap[F[_]](service: HttpService[F]): HttpService[F] =
    cats.data.Kleisli { req: Request[F] =>
      logRequest(req)
      service(req)
    }

  def apply[F[_]](pf: PartialFunction[Request[F], F[Response[F]]])(implicit F: Applicative[F]): HttpService[F] =
    wrap(HttpService[F](pf))

  /** Log info about a request */
  private[this] def logRequest[F[_]](req: Request[F]): Unit =
    println(s"Call to ${req.uri.toString()}")
}
