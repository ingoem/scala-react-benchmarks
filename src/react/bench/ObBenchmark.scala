package react.bench

import java.util.concurrent.ConcurrentLinkedQueue

abstract class ObBenchmark extends Benchmark with App { outer =>
  def flush() {}

  private val asyncTodos = new ConcurrentLinkedQueue[()=>Unit]
  private val runnable = new Runnable { def run() = outer.run() }

  def innerLoop(iters: Int) {
    var j = 0
    while (j < iters) {
      run()
      j += 1
    }
  }

  def schedule(op: =>Unit) { asyncTodos.add(()=>op) }
  def commit(op: =>Unit) {
    schedule(op)
    runTurn()
  }

  def runTurn() {
    var t = asyncTodos.poll()
    while (t ne null) {
      t()
      t = asyncTodos.poll()
    }
  }

  override def main(args: Array[String]) = {
    super.main(args)
    runBenchmark()
  }
}