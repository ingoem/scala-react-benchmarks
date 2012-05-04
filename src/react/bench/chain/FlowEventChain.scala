package react.bench
package chain

import BenchDomain._

object FlowEventChain extends ReactiveBenchmark with Observing {
  val depth = prop("depth", 100)
  var res = 0d
  val x = EventSource[Double]
  var sum: Events[Double] = x
  for (i <- 1 to depth) {
    val y = sum
    sum = if (i % 2 == 0) Events.loop[Double] { self =>
      val v = (self await y) + random.nextDouble
      self << v
      self.pause
    }
    else Events.loop[Double] { self =>
      val v = (self await y) - random.nextDouble
      self << v
      self.pause
    }
  }
  observe(sum) { h => res += h }

  def run() {
    x << random.nextDouble
  }

  def done {
    log("Result " + res)
  }
}