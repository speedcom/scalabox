sealed trait FileState {
  def inc(): Copying = ???
  def dec(): FileState = ???
}
case object Sterile extends FileState {
  override def inc(): Copying = Copying(n = 1)
}
case object Created extends FileState
case class Copying(n: Int) extends FileState {
  override def inc(): Copying = copy(n + 1)
  override def dec(): FileState = if(n > 1) Copying(n-1) else Sterile
}
case object Deleted extends FileState