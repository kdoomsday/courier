package sources

import akka.stream.actor.ActorPublisher
import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.Executor

/**
 * ActorPublisher que manda los archivos de un directorio.
 * Utiliza akka reactive streams; es un Publisher en este sentido.
 */
class FileSource(path: String) extends ActorPublisher[File] {
  import akka.stream.actor.ActorPublisherMessage._

  var files = getFiles()

  def receive = {
    case Request(_) => deliver()
    case Cancel     => context.stop(self)
  }

  private def getFiles() = {
    val home = new File(path)
    home.listFiles().toList
  }

  def deliver(): Unit = {
    if (files.isEmpty)
      onCompleteThenStop()
    else if (totalDemand > 0) {
      val toSend = if (totalDemand < Int.MaxValue) totalDemand.toInt
      else Int.MaxValue

      val (use, keep) = files.splitAt(toSend)
      use.foreach { f => onNext(f) }
      files = keep

      if (files.isEmpty) onCompleteThenStop()
    }
  }
}
