package react.bench
package chain

object ObChain extends ObBenchmark {
  val depth = prop("depth", 100)
  var res = 0d
  val x = new Observable(0d)
  var sum: Observable[Double] = x
  for (i <- 1 to depth) {
    val y = new Observable(0d)
    if (i % 2 == 0) sum += { v => y.value = v + random.nextDouble }
               else sum += { v => y.value = v - random.nextDouble }
    sum = y
  }
  sum += { h => res += h }

  def run() {
    x.value = random.nextDouble
  }

  def done {
    log("Result " + res)
  }
}