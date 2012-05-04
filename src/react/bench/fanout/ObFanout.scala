package react.bench
package fanout

object ObFanout extends ObBenchmark {
  val fanout = prop("fanout", 100)
  var res = 0d
  var received = 0
  val x = new Observable(0d)

  Array.tabulate(fanout) { i =>
    val y = new Observable(0d)
    if (i % 2 == 0) x += { v => y.value = v + random.nextDouble }
               else x += { v => y.value = v - random.nextDouble }
    y += { h =>
      received += 1;
      res += h
    }
  }

  def run() {
    x.value = random.nextDouble
  }

  def done {
    log("Result " + res, ", received " + received)
  }
}