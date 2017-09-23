package courier.client.io

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.stream.alpakka.file.scaladsl.Directory
import java.nio.file.{ Files, Path, Paths }

/** Fuente de archivos para la transmisión */
trait FileSource {
	/** Generar un Stream con los archivos que se van a procesar */
	def find(): Source[Path, NotUsed]
}

/** Obtiene Source[Path, NotUsed] a partir de una ruta */
class DefaultFileSource(val basePath: Path) extends FileSource {
  require(Files.isDirectory(basePath))

  def this(path: String) = this(Paths.get(path))

	def find(): Source[Path, NotUsed] = Directory.walk(basePath)
}
object DefaultFileSource {
  def apply(basePath: String) = new DefaultFileSource(basePath)
}
