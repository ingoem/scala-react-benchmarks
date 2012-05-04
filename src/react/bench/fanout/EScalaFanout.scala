package react.bench
package fanout

import events.lib._

object EScalaFanout extends EScalaBenchmark {
  val fanout = prop("fanout", 100)
  var res = 0d
  var received = 0
  val x = new ImperativeEvent[Double]
  Array.tabulate(fanout) { i =>
    val es = if (i % 2 == 0) x map { (v: Double) => v + random.nextDouble }
                        else x map { (v: Double) => v - random.nextDouble }
    es += { h =>
      received += 1;
      res += h
    }
  }

  def run() {
    x(random.nextDouble)
  }
  def done() {
    log("Result " + res, ", received " + received)
  }
}

