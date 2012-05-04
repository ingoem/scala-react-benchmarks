package react.bench
package drag

import BenchDomain._

abstract class ReactDragBench extends ReactiveBenchmark2 with Observing {
  val take = prop("take", 10)
  val excess = prop("excess", 100)

  val down = EventSource[Event]
  val move = EventSource[Event]
  val up = EventSource[Event]

  def drag: Events[Event]

  var res = 0d
  var received = 0

  def observeResult(es: Events[Event]) = observe(es) { e =>
    received += 1
    res += e.x + e.y
  }

  def run() {
    commit { down << Event(random.nextDouble, random.nextDouble) }

    var i = 0
    while (i < take) {
      commit { move << Event(random.nextDouble, random.nextDouble) }
      i += 1
    }
    commit { up << Event(random.nextDouble, random.nextDouble) }

    var j = 0
    while (j < excess) {
      commit { move << Event(random.nextDouble, random.nextDouble) }
      j += 1
    }
  }

  def done() = log("Receive count: " + received + ", result: " + res)
}