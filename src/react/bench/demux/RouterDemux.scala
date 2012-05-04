package react.bench
package demux

import BenchDomain._

object RouterDemux extends ReactiveBenchmark with Observing {
  val es = EventSource[Int]
  val fanout = 200
  val router = new Router(es) {
    val sinks = Array.tabulate(fanout) { i => EventSource[Int] }
    def react() {
      es.ifEmitting { x =>
        sinks(x) << x
      }
    }
  }

  var sum = 0L
  for (s <- router.sinks) {
    observe(s) { x =>
      sum += x
    }
  }

  def run() {
    es << random.nextInt(fanout)
  }

  def done() = log(sum)
}