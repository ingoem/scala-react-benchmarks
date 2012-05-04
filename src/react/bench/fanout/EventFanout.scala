package react.bench
package fanout

import BenchDomain._

object EventFanout extends ReactiveBenchmark with Observing {
  val fanout = prop("fanout", 100)
  var res = 0d
  var received = 0
  val x = EventSource[Double]
  Array.tabulate(fanout) { i =>
    val es = if (i % 2 == 0) x map { _ + random.nextDouble }
                        else x map { _ - random.nextDouble }
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

object EventFanoutCollect extends ReactiveBenchmark with Observing {
  val fanout = 100
  var res = 0d
  var received = 0
  val x = EventSource[Double]
  Array.tabulate(fanout) { i =>
    val es = if (i % 2 == 0) x collect { case x => x + random.nextDouble }
                        else x collect { case x => x - random.nextDouble }
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