package react.bench
package demux

import events.lib._

object EScalaDemux extends EScalaBenchmark {
  val es = new ImperativeEvent[Int]
  val fanout = 200
  val sinks = Array.tabulate(fanout) { i =>
    es && { _ == i }
  }
  var sum = 0L
  for (s <- sinks) {
    s += { sum += _ }
  }

  def run() {
    es(random.nextInt(fanout))
  }
  def done() {
    log(sum)
  }
}

