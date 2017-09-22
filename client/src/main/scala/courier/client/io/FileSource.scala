package courier.client.io

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.Source
import java.nio.file.Path

/** Fuente de archivos para la transmisión */
trait FileSource {
	/** Generar un Stream con los archivos que se van a procesar */
	def find(): Source[Path, NotUsed]
}

class DefaultFileSource(basePath: String) extends FileSource {
	def find(): Source[Path, NotUsed] = ???
}
