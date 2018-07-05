package courier

import cats.effect._
import fs2.StreamApp.ExitCode
import fs2.{ Stream, StreamApp }
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext.Implicits.global

object CourierServer extends StreamApp[IO] {

  val helloService = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")
  }

  def shutdownService(requestShutdown: IO[Unit]) = HttpService[IO] {
    case GET -> Root / "shutdown" =>
      requestShutdown.unsafeRunSync()
      Ok()
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(9000, "localhost")
      .mountService( helloService )
      .mountService( shutdownService(requestShutdown) )
      .serve
  }
}
