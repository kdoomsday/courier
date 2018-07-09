package courier.client

import cats.effect.IO
import org.http4s.Uri
import org.http4s.client.blaze.Http1Client

/** Cliente básico que se comunica con el servidor */
object Http4sClient extends App {

  val client = Http1Client[IO]().unsafeRunSync()

  val name = "eduardo"

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  val target = Uri.uri("http://localhost:9000/hello/") / name

  val response = client.expect[String](target)

  println(response.unsafeRunSync())
}
