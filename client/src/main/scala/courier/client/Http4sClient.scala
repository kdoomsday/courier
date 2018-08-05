package courier.client

import cats.effect.IO
import courier.auth.{ AuthToken, ClientId }
import courier.client.auth.AuthDaoRest
// import _root_.io.circe.Decoder
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.client.blaze.Http1Client
import _root_.io.circe.generic.auto._
import org.http4s.circe._

/** Cliente básico que se comunica con el servidor */
object Http4sClient extends App {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  val base = Uri.uri("http://localhost:9000/")

  // // implicit val tokenDecoder: Decoder[AuthToken] = deriveDecoder[AuthToken]
  // implicit val decoder = jsonOf[IO, AuthToken]

  val client = Http1Client[IO]().unsafeRunSync()

  // val name = "eduardo"

  // @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  // val target = base / "hello" / name

  // // val request = GET(
  // //   base / "authenticate",
  // //   Header("X-Header(, value: String)
  // // )
  // val response = client.expect[String](target)

  // println(response.unsafeRunSync())

  val dao = AuthDaoRest(client, base)
  val token = dao.getToken(ClientId("123")).unsafeRunSync()
  println(token)
}
