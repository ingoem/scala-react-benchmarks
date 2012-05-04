package react.bench
package drag

import BenchDomain._

object ObDragging extends ObBenchmark with Observing {
  val take = prop("take", 10)
  val excess = prop("excess", 100)

  val down = new Observable[Event]
  val move = new Observable[Event]
  val up = new Observable[Event]
  val drag = new Observable[Event]

  var startX, startY = 0d
  var moveOb = { (mm: Event) =>
    drag.value = Event(mm.x - startX, mm.y - startY)
  }

  down += { md =>
    startX = md.x
    startY = md.y

    move += moveOb
  }

  up += { md =>
    move -= moveOb
  }

  var res = 0d
  var received = 0
  drag += { e =>
    received += 1
    res += e.x + e.y
  }

  def run() {
    commit { down.value = Event(random.nextDouble, random.nextDouble) }

    var i = 0
    while(i < take) {
      commit { move.value = Event(random.nextDouble, random.nextDouble) }
      i += 1
    }
    schedule { up.value = Event(random.nextDouble, random.nextDouble) }

    var j = 0
    while(j < excess) {
      commit { move.value = Event(random.nextDouble, random.nextDouble) }
      j += 1
    }
  }

  def done() = log("Receive count: " + received + ", result: " + res)
}