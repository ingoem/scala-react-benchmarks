package react.bench

import scala.react._

object BenchDomain extends Domain {
  val scheduler = new ManualScheduler
  val engine = new Engine
}
import BenchDomain._

abstract class ReactiveBenchmark extends Benchmark with ReactiveApp { outer =>

  private val runnable = new Runnable { def run() = outer.run() }

  def innerLoop(iters: Int) {
    var j = 0
    while (j < iters) {
      schedule(runnable)
      engine.runTurn()
      j += 1
    }
  }

  protected def flush() = engine.runTurn()
  override def main() = runBenchmark()
  def commit(op: =>Unit) {
    schedule(op)
    runTurn()
  }
}