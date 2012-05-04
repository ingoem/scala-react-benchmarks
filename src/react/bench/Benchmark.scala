package react.bench

import java.util.Random
import collection.mutable.LinkedHashMap

abstract class Benchmark {
  val warmups = prop("bench.warmups", 5) // outer iterations in warmup phase
  val repeats = prop("bench.counts", 10) // outer iterations
  val iters = prop("bench.iters", 100000) // inner iterations
  val standalone = prop("bench.standalone", true)

  private lazy val properties = new LinkedHashMap[String, String]

  def prop[A: Manifest](name: String, default: A): A = {
    val str = System.getProperty(name, default.toString)
    val StringManifest = classManifest[String]
    val res = manifest[A] match {
      case ClassManifest.Int => str.toInt
      case ClassManifest.Long => str.toLong
      case ClassManifest.Double => str.toDouble
      case ClassManifest.Float => str.toFloat
      case ClassManifest.Boolean => str.toBoolean
      case ClassManifest.Short => str.toShort
      case ClassManifest.Byte => str.toByte
      case StringManifest => str
      case c =>
        throw new IllegalArgumentException("Cannot handle property type " + c)
    }
    properties(name) = str
    res.asInstanceOf[A]
  }

  val random = new Random(12345)

  def run()
  def done()

  protected def innerLoop(iters: Int) // hot loop goes here
  protected def flush()

  def measure(count: Int): Array[Double] = {
    val results = new Array[Double](count)
    var i = 0
    while (i < count) {
      val t0 = System.currentTimeMillis
      innerLoop(iters)
      val t = System.currentTimeMillis - t0
      results(i) = t.toDouble
      i += 1
    }
    results
  }

  def log(any: Any) = {
    print("> ")
    println(any)
  }
  def explain(any: Any) {
    println(any)
  }

  /**
   * Runs this benchmark. When run in standalone mode, it prints verbose information. When run
   * as part of a suite, it only prints the running times for each outer iterations,
   * delimited by line breaks. The latter is used for simple IPC between a benchmark suite and a
   * child process.
   */
  def runBenchmark() {
    flush()
    explain("Benchmark " + getClass.getSimpleName)
    if(!properties.isEmpty)
      explain("Arguments: " + properties.foldLeft("") { (res, p) => res + p._1 + "=" + p._2 + " " })
    explain("Running " + repeats + " times with " + iters + " iterations each.")

    explain("Warming up " + warmups + " times...")
    measure(warmups)

    explain("Running " + repeats + " times...")
    var results = measure(repeats)
    explain("------- done ----------")
    done()
    explain("------ Results --------")
    results = results map (_ / 1000)

    explain("Time in seconds and iters/sec per run:")
    if (standalone)
      results foreach { res =>
        log(("%.3f" format res) + "   " + ("%.0f" format iters / res))
      }
    else
      results foreach { res => log("%.3f" format res) }

    val mean = Stats.mean(results)
    val stdev = Stats.stdDev(mean, results)
    explain("Average time: " + mean)
    explain("Stddev time: " + ("%.6f" format stdev))

    val iterresults = results.map { iters / _ }
    val itermean = Stats.mean(iterresults)
    val iterstdev = Stats.stdDev(itermean, iterresults)
    explain("Average iters/sec: " + ("%.0f" format itermean))
    explain("Stddev iters/sec: " + ("%.2f" format iterstdev))
  }

  def warmup(warmups: Int) {
    measure(warmups)
  }

  def runBenchmark(repeats: Int, iters: Int) {
    var results = measure(repeats, iters)
    results foreach { res => log("%.3f" format res) }
  }

  def measure(repeats: Int, iters: Int): Array[Double] = {
    // don't surprise JIT, so no fancy here: stick to arrays and while loops
    val results = new Array[Double](repeats)
    var i = 0
    while (i < repeats) {
      val t0 = System.currentTimeMillis
      innerLoop(iters)
      val t = System.currentTimeMillis - t0
      results(i) = t.toDouble
      i += 1
    }
    results
  }

  //def main(args: Array[String])
}