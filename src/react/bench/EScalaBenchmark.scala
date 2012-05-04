package react.bench

import java.util.concurrent.ConcurrentLinkedQueue

abstract class EScalaBenchmark extends Benchmark with App { outer =>
  def flush() {}

  private val asyncTodos = new ConcurrentLinkedQueue[Runnable]
  private val runnable = new Runnable { def run() = outer.run() }

  def innerLoop(iters: Int) {
    var j = 0
    while (j < iters) {
      asyncTodos add runnable
      runTurn()
      //schedule(runnable)
      //engine.runTurn()
      j += 1
    }
  }

  def runTurn() {
    var t = asyncTodos.poll()
    while (t ne null) {
      t.run()
      t = asyncTodos.poll()
    }
  }

  override def main(args: Array[String]) = {
    super.main(args)
    runBenchmark()
  }
}