package react.bench
package fanout

import BenchDomain._

object FlowEventFanout extends ReactiveBenchmark with Observing {
  val fanout = prop("fanout", 100)
  var res = 0d
  var received = 0
  val x = EventSource[Double]
  Array.tabulate(fanout) { i =>
    val es = if (i % 2 == 0) Events.loop[Double] { self =>
      val v = (self await x) + random.nextDouble
      self << v
      self.pause
    }
    else Events.loop[Double] { self =>
      val v = (self await x) - random.nextDouble
      self << v
      self.pause
    }
    observe(es) { h =>
      received += 1;
      res += h
    }
  }

  def run() {
    x << random.nextDouble
  }

  def done {
    log("Result " + res, ", received " + received)
  }
}