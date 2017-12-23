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

/** Obtiene Source[Path, NotUsed] a partir de una ruta
  * @param basePath Ruta base a partir de la cual se cargan los archivos
  * @param withDirs Si se debe incluir directorios en los elementos emitidos
  */
class DefaultFileSource(val basePath: Path, withDirs: Boolean) extends FileSource {
  require(Files.isDirectory(basePath))

  def this(path: String) = this(Paths.get(path), true)
  def this(path: String, withDirs: Boolean) = this(Paths.get(path), withDirs)

	def find(): Source[Path, NotUsed] =
    if (withDirs) Directory.walk(basePath)
    else          Directory.walk(basePath).filter(p => !Files.isDirectory(p))
}
object DefaultFileSource {
  def apply(basePath: String) = new DefaultFileSource(basePath)
  def apply(basePath: String, withDirs: Boolean) = new DefaultFileSource(basePath, withDirs)
}
