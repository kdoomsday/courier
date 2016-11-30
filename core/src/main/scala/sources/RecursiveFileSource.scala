package sources

import akka.stream.actor.ActorPublisher
import java.io.File
import akka.stream.actor.ActorPublisherMessage.Request
import akka.stream.actor.ActorPublisherMessage.Cancel
import akka.actor.ActorLogging
import akka.actor.Props
import akka.stream.scaladsl.Source
import akka.actor.ActorRef

class RecursiveFileSource(val root: String, val filter: File => Boolean)
    extends ActorPublisher[File]
    with ActorLogging {
  import scala.collection.{ mutable => m }

  // Constructor por defecto, acepta todos los archivos
  def this(root: String) = this(root, _ => true)

  private[this] val queue = m.Queue[File]()

  def initialize() = {
    val f = new File(root)
    if (f.exists() && f.isDirectory())
      fillFrom(f)
  }
  initialize()

  // Fill with all files inside a path (non-recursively). Assumes f exists and is directory
  private[this] def fillFrom(f: File) =
    if (f.listFiles() != null)
      queue ++= f.listFiles().filter(filter)

  // Enviar los archivos al stream
  private[this] def deliver() = {
    while (isActive && totalDemand > 0) {
      if (queue.isEmpty) onComplete()
      else {
        val f = queue.dequeue()
        if (f.isDirectory()) fillFrom(f)
        onNext(f)
      }
    }
  }

  def receive = {
    case Request(amnt) => {
      log.debug(s"Request($amnt)")
      deliver()
    }
    case Cancel => {
      log.debug("Cancel")
      context.stop(self)
    }
  }
}

object RecursiveFileSource {

  /**
   * Obtiene un Source[File, _] respaldado por un RecursiveFileSource, que acepta los archivos
   *  segun filter
   */
  def apply(path: String, filter: File ⇒ Boolean): Source[File, ActorRef] =
    Source.actorPublisher[File](Props(classOf[RecursiveFileSource], path, filter))

  /** Obtiene un Source[File, _] respaldado por un RecursiveFileSource, que acepta todos los archivos */
  def apply(path: String): Source[File, ActorRef] = apply(path, _ ⇒ true)
}
