package react.bench
package demux

import BenchDomain._

object FRPDemux extends ReactiveBenchmark with Observing {
  val es = EventSource[Int]
  val fanout = 200
  val sinks = Array.tabulate(fanout) { i =>
    es filter { _ == i }
  }
  var sum = 0L
  for (s <- sinks) {
    observe(s) { sum += _ }
  }

  def run() {
    es << random.nextInt(fanout)
  }

  def done() = log(sum)
}