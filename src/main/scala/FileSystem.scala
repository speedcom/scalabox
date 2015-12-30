import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter

import scala.concurrent.stm._
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.io.File
import scala.collection.JavaConverters._

class FileSystem(val rootPath: String) {
  private val files = TMap[String, FileInfo]()

  def init() = atomic { implicit txn =>
    files.clear()

    FileUtils
      .iterateFilesAndDirs(new File(rootPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
      .asScala
      .foreach { f =>
        val fileInfo = FileInfo(f)
        files(fileInfo.path) = fileInfo
      }
  }

  def getAll() = atomic { implicit thx => files.values }

  def getListDir(dir: String) = atomic { implicit txn =>
    files.filter { case (_, fi) => fi.parentDir == dir }
  }

  def copyFile(src: String, dest: String) = atomic { implicit txn =>
    if(files.contains(dest))
      sys.error(s"Destination exists")

    val info = files(src)

    info.state match {
      case Sterile | Copying(_) =>
        val srcFile  = new File(src)
        val destFile = new File(dest)
        files(src)   = info.copy(state = info.state.inc())
        files(dest)  = FileInfo.creating(destFile)
        Txn.afterCommit { _ => copyOnDisk(srcFile, destFile) }
    }
  }

  private def copyOnDisk(srcFile: File, destFile: File) = {
    FileUtils.copyFile(srcFile, destFile)

    atomic { implicit txn =>
      val ninfo = files(srcFile.getPath)
      files(srcFile.getPath)  = ninfo.copy(state = ninfo.state.dec())
      files(destFile.getPath) = FileInfo(destFile)
    }
  }

  def deleteFile(src: String) = atomic { implicit txn =>
    val info = files(src)
    info.state match {
      case Sterile =>
        files(src) = info.copy(state = Deleted)
        Txn.afterCommit { _ =>
          FileUtils.forceDelete(new File(src))
          files.single.remove(src)
        }
    }
  }

}