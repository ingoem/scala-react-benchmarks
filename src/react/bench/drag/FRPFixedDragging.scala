package react.bench
package drag

import BenchDomain._

object FRPFixedDragging extends ReactDragBench {
  var startX, startY = 0d

  //val inner = move.map { mm =>
  //  Event(mm.x - startX, mm.y - startY)
  //}
  val inner = move.collect { case mm =>
    Event(mm.x - startX, mm.y - startY)
  }

  val moveEe = down map { md =>
    startX = md.x
    startY = md.y
    inner
  }

  val dropEe = up map { x => Events.never[Event] }

  val drag = (moveEe merge dropEe).flatten

  observeResult(drag)
}