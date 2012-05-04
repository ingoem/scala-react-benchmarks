package react.bench
package chain
import BenchDomain._

object EventChain extends ReactiveBenchmark with Observing {
  val depth = prop("depth", 100)
  var res = 0d
  val x = EventSource[Double]
  var sum: Events[Double] = x
  for (i <- 1 to depth) {
    val y = sum
    sum = if (i % 2 == 0) y map { _ + random.nextDouble }
                     else y map { _ - random.nextDouble }
  }
  observe(sum) { h => res += h }

  def run() {
    x << random.nextDouble
  }

  def done {
    log("Result " + res)
  }
}

object EventChainCollect extends ReactiveBenchmark with Observing {
  val depth = 100
  var res = 0d
  val x = EventSource[Double]
  var sum: Events[Double] = x
  for (i <- 1 to depth) {
    val y = sum
    sum = if (i % 2 == 0) y collect { case x => x + random.nextDouble }
                     else y collect { case x => x - random.nextDouble }
  }
  observe(sum) { h => res += h }

  def run() {
    x << random.nextDouble
  }

  def done {
    log("Result " + res)
  }
}