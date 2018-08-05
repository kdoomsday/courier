package courier.client.auth

import cats.effect.IO
import courier.auth.{ AuthToken, ClientId }
import org.http4s.{ Uri, UrlForm }
import org.http4s.client.Client
import org.http4s.dsl._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import _root_.io.circe.syntax._
import _root_.io.circe.generic.auto._

/** Impl de AuthDao que llama a un servicio Rest para obtener respuestas */
case class AuthDaoRest(client: Client[IO], uriBase: Uri) extends AuthDao {

  // Decoders para obtener las entidades
  implicit val decoder = jsonOf[IO, AuthToken]

  // Implementaciones //

  def getToken(creds: ClientId): IO[AuthToken] = {
    val request = Http4sDsl[IO].POST(
      uriBase / "authenticate",
      UrlForm("auth" -> creds.asJson.toString())
    )

    client.expect[AuthToken](request)
  }
}
