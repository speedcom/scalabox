case class FileInfo(
  name: String,
  path: String,
  parentDir: String,
  isDir: Boolean,
  state: FileState
)

object FileInfo {
  import java.io.File

  def apply(f: File): FileInfo = FileInfo(
     name = f.getName,
     path = f.getPath,
     parentDir = f.getParent,
     isDir = f.isDirectory,
     state = Sterile)

  def creating(f: File): FileInfo = FileInfo(
    name = f.getName,
    path = f.getPath,
    parentDir = f.getParent,
    isDir = f.isDirectory,
    state = Created)

}