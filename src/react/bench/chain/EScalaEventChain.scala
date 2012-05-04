package react.bench
package chain

import events.lib._

object EScalaEventChain extends EScalaBenchmark {
  val depth = prop("depth", 100)
  val x = new ImperativeEvent[Double]
  var sum: EventNode[Double] = x
  for (i <- 1 to depth) {
    val y = sum
    sum = if (i % 2 == 0) y map { (v: Double) => v + random.nextDouble }
                     else y map { (v: Double) => v - random.nextDouble }
  }
  var res = 0d
  sum += { res += _ }

  def run() {
    x(random.nextDouble)
  }
  def done() {
    log(res)
  }
}

