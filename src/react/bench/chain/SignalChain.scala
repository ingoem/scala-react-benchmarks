package react.bench
package chain

import BenchDomain._

object SignalChain extends ReactiveBenchmark with Observing {
  def Sig[A](body: => A): Signal[A] = if(strict) Strict(body) else Lazy(body)

  val strict = prop("strict", false)
  val depth = prop("depth", 100)
  var res = 0d
  val source = Var(0d)
  var walk: Signal[Double] = source
  for (i <- 1 to depth) {
    val y = walk
    walk = if (i % 2 == 0) Sig { y() + random.nextDouble }
    else Sig { y() - random.nextDouble }
  }
  observe(walk) { h => res += h }

  def run() {
    source() = random.nextDouble
  }

  def done {
    log("Result " + res)
  }
}