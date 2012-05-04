package react.bench
package fanout

import BenchDomain._

object SignalFanout extends ReactiveBenchmark with Observing {
  def Sig[A](body: => A): Signal[A] = if(strict) Strict(body) else Lazy(body)

  val strict = prop("strict", false)
  val fanout = prop("fanout", 100)
  var res = 0d
  var received = 0
  val x = Var(0d)
  Array.tabulate(fanout) { i =>
    val es = if (i % 2 == 0) Sig { x() + random.nextDouble }
                        else Sig { x() - random.nextDouble }
    observe(es) { h =>
      received += 1;
      res += h
    }
  }

  def run() {
    x() = random.nextDouble
  }

  def done {
    log("Result " + res, ", received " + received)
  }
}