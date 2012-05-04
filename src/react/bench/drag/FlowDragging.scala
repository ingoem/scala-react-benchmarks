package react.bench
package drag

import BenchDomain._

object FlowDragging extends ReactDragBench {
  val drag = Events.loop[Event] { self =>
    val md = self await down
    self.loopUntil(up) {
      val mm = self awaitNext move
      self << Event(mm.x - md.x, mm.y - md.y)
    }
    ()
  }

  observeResult(drag)
}