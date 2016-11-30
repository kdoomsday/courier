package sources

import java.io.File
import akka.stream.actor.ActorPublisher
import sample.fileiter.RepeatableFileSource.FileMessage

/**
 * Implementa un Source de archivos que itera por toda la estructura de un directorio. Cuando
 *  el arbol termina envia un mensaje de EndTree y comienza de nuevo.
 */

object RepeatableFileSource {
  sealed trait FileMessage
  case class Archivo(file: File) extends FileMessage
  case object EndTree extends FileMessage
}

class RepeatableFileSource(path: String) extends ActorPublisher[FileMessage] {
  import akka.stream.actor.ActorPublisherMessage._
  import RepeatableFileSource.{ Archivo, EndTree }

  private[this] val files = scala.collection.mutable.Queue.empty[File]

  // Add all subfiles of f to the queue (f is not added)
  def fillFrom(f: File) =
    if (f.exists() && f.isDirectory() && f.listFiles() != null)
      files ++= f.listFiles()

  // Llenar el buffer con la primera lista de archivos
  private[this] def initialFill() = fillFrom(new File(path))

  // Llenar inicialmente el buffer
  initialFill()

  def receive = {
    case Request(_) => deliver()
    case Cancel     => context.stop(self)
  }

  private[this] def deliver() = {
    if (totalDemand > 0) {
      if (files.isEmpty) {
        onNext(EndTree)
        initialFill()
      } else {
        for (i <- 0 until totalDemand.toInt if !files.isEmpty) {
          val f = files.dequeue()
          fillFrom(f)
          onNext(Archivo(f))
        }
      }
    }
  }
}
