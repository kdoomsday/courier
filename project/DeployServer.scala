object DeployServer {
  import scala.util.{ Try, Success, Failure }
  import java.io.{ File, IOException, FileInputStream, FileOutputStream }
  import java.nio.file.{ Files, Path, Paths }
  import java.util.stream.Stream
  import scala.collection.JavaConverters._
  import java.util.zip.{ ZipInputStream, ZipEntry }

  /** Delete all the contents of a dir. Return whether it was successful */
  def delDirContents(dir: Path): Boolean = {
    val files: Stream[Path] = Files.walk(dir)
    try {
      for (f: Path <- files.toList.reverse)
        if (f != dir) Files.delete(f)
      true
    }
    catch {
      case e : IOException =>
        println("Couldn't delete files: " + e.getMessage)
        false
    }
    finally {
      files.close
    }
  }
  def delDirContents(f: File): Boolean = delDirContents(f.toPath)

  /** Conseguir los archivos inmediatos de una ruta que terminan en una extension */
  def findExts(where: Path, suffix: String): List[Path] =
    Files.list(where).toList.filter(_ endsWith suffix)

  /** Copiar el zip del servidor al directorio destino */
  def copyZips(from: Path, to: Path) = {
    println(s"Copy zips from $from to $to")
    for (path <- findExts(from, "zip").toList) {
      println(s"Copy $from")
      Files.copy(path, to)
    }
  }

  /** Copy a file
    * @return the Path of the resulting file
    */
  def copy(what: Path, where: Path): Try[Path] =
    Try { Files.copy(what, where.resolve(what.getFileName.toString)) }

  /** Unzip a file where it stands */
  def unzip(item: Path): Try[Unit] = Try {
    val zis = new ZipInputStream(new FileInputStream(item.toFile))

    val buffer = new Array[Byte](1024)

    var ze: ZipEntry = zis.getNextEntry()
    while (ze != null) {
      val filename = ze.getName()
      val newFile = Paths.get(item.getParent.toString, filename)

      if ( ! ze.isDirectory ) {
        Files.createDirectories(newFile.getParent)
        val fos = new FileOutputStream(newFile.toFile)

        var len = zis.read(buffer)
        while (len > 0) {
          fos.write(buffer, 0, len)
          len = zis.read(buffer)
        }

        fos.close()
      }
      else {
        Files.createDirectories(newFile)
      }

      ze = zis.getNextEntry()
    }

    zis.closeEntry()
    zis.close()
  }

  def deploy(zipPath: Path, deployPath: Path): Try[Unit] = {
    Files.createDirectories(deployPath)
    for {
      newZipPath <- copy(zipPath, deployPath)
      _ <- unzip(newZipPath)
    } yield ()
  }


  /* *** *** *** *** Conversiones *** *** *** *** */
  // Para convertir Stream a Lista scala
  implicit class Stream2List[A](s: java.util.stream.Stream[A]) {
    def toList = s.iterator.asScala.toList
  }
}
