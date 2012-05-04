package react.bench
package drag

import BenchDomain._

object FRPDragging extends ReactDragBench {
  val moveEe = down map { md =>
    val startX = md.x
    val startY = md.y

    move.map { mm =>
      Event(mm.x - startX, mm.y - startY)
    }
  }

  val dropEe = up map { x => Events.never[Event] }

  val drag = (moveEe merge dropEe).flatten

  observeResult(drag)
}