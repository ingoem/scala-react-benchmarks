package react.bench

import collection.mutable.ArrayBuffer

case class Event(x: Double, y: Double)

class Observable[T](private var v: T) {
  def this() = this(null.asInstanceOf[T])

  private val obs = new ArrayBuffer[T=>Unit]

  def +=(ob: T=>Unit) {
    if (obs contains ob) return;
    obs += ob
  }
  def -=(ob: T=>Unit) {
    obs -= ob
  }

  def value = v
  def value_=(t: T) {
    v = t
    sendEvent()
  }

  protected def sendEvent() {
    obs foreach { f => f(v) }
  }
}