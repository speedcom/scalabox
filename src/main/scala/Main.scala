object Main extends App {

  val rootPath = getClass.getResource("data").getPath
  val fileSystem = new FileSystem(rootPath)

  // INIT FILESYSTEM
  fileSystem.init()

  // LIST FILES
  assert(fileSystem.getListDir(rootPath).size == 6)

  // COPY FILE
  assert(fileSystem.getListDir(s"$rootPath/a").size == 2)
  assert(fileSystem.getListDir(s"$rootPath/b").size == 2)
  fileSystem.copyFile(src = s"$rootPath/a/a.txt", dest = s"$rootPath/b/a.txt")
  assert(fileSystem.getListDir(s"$rootPath/a").size == 2)
  assert(fileSystem.getListDir(s"$rootPath/b").size == 3)

  // DELETE FILE
  assert(fileSystem.getListDir(s"$rootPath/c").size == 2)
  fileSystem.deleteFile(s"$rootPath/c/c2.txt")
  assert(fileSystem.getListDir(s"$rootPath/c").size == 1)

}