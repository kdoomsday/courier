import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import fs2.{ Stream, StreamApp }
import StreamApp.ExitCode
import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] {

  val helloService = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
      BlazeBuilder[IO].bindHttp(9000, "localhost")
          .mountService(helloService)
          .serve
}
