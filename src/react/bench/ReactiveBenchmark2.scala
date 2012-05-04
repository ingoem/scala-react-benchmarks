package react.bench

import BenchDomain._

abstract class ReactiveBenchmark2 extends Benchmark with ReactiveApp { outer =>

  def innerLoop(iters: Int) {
    var j = 0
    while (j < iters) {
      run()
      j += 1
    }
  }

  def runTurn() = engine.runTurn()

  protected def flush() = engine.runTurn()
  override def main() = runBenchmark()
  def commit(op: =>Unit) {
    schedule(op)
    runTurn()
  }
}