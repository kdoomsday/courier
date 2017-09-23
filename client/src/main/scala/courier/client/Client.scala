object Client extends App {
  import Config._
  import akka.NotUsed
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl.{ Flow, RunnableGraph, Sink, Source }

  implicit val system = ActorSystem("Flows")
  implicit val materializer = ActorMaterializer()

  val flowSum = Flow[Int].fold(0)(_ + _)
  val flowMult = Flow[Int].fold(1)(_ * _)

  val flow: RunnableGraph[NotUsed] = Source(1 to 10)
    .alsoTo(flowSum.to(Sink.foreach(println(_))))
    .to(flowMult.to(Sink.foreach(println(_))))

  val res: NotUsed = flow.run
  println(s"Client says: $msg")
}
