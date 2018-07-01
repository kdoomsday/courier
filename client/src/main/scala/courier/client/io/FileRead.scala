package courier.client.io

import java.nio.file.{ Files, Path }
import scala.util.Try

/** Lector de archivos. Parametriza lo que devuelve el lector */
trait FileReader[T] {
  def read(file: Path): T
}

/** Permite extraer los bytes de un archivo */
object FileRead extends FileReader[Try[Array[Byte]]] {
  def read(file: Path): Try[Array[Byte]] = Try {
    Files.readAllBytes(file)
  }
}
