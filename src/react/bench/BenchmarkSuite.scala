package react.bench

import java.io.InputStreamReader
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.BufferedReader
import java.io.IOException
import collection.JavaConversions
import collection.mutable.ArrayBuffer

abstract class BenchmarkSuite extends ProcessTools {
  def tests: Seq[TestDef]
  def vmInvokes: Int = 5 // number of repeated runs of the same test in separate VMs
  def dropFirst = true

  def warmups = 5
  def repeats = 10
  def iters = 100000

  case class TestDef(benchmark: Benchmark)(val args: (String, Any)*)

  def main(args: Array[String]) {
    new Terminator().start()

    val java = "java"
    val globalVMArgs = "-Dbench.standalone=false"
    val verbose = sys.props.getOrElse("bench.verbose", "false").toBoolean
    val cp = sys.props("java.class.path")
    val bcp = sys.props("sun.boot.class.path")

    println("Starting benchmark suite in working directory " + System.getProperty("user.dir"))
    println("Current classpath: " + cp)
    println("===== Press enter to shutdown gracefully. =====")

    val cmdPrefix = Array(java, "-Xbootclasspath:" + bcp, "-cp", cp, "-Dscala.usejavacp=true", globalVMArgs,
        "-Dbench.warmups=" + warmups, "-Dbench.counts=" + repeats, "-Dbench.iters=" + iters)

    println("Running tests with: " + cmdPrefix.mkString(" "))

    for (t <- tests) {
      println()
      val rawClassName = t.benchmark.getClass.getName
      val className = rawClassName.substring(0, rawClassName.length - 1)

      val cmd = cmdPrefix ++ (t.args map { a => "-D" + a._1 + "=" + a._2 }) ++ Array(className)

      println("Running " + className + " ...")
      val results = new ArrayBuffer[Double]
      for (i <- 1 to vmInvokes) {
        println("-------------------- Run " + i + " ----------------------")
        val p = startProcess(cmd)
        p pipe { line =>
          if (line.startsWith("> ")) {
            try {
              val d = line.drop(2).toDouble
              if (!dropFirst || i > 1) results += d
            } catch {
              case e: NumberFormatException => println(line)
            }
          } else if(verbose) {
            println(line)
          }
        }
      }
      println()
      println("------------- Statistics for " + className + " ---------------------------")
      if (dropFirst) println("Dropping result from first VM invocation...")
      println("Collected " + results.length + " results from " + vmInvokes + " VM invocations.")
      println("Running " + repeats + " times with " + iters + " iterations each.")
      println("Arguments: " + t.args.foldLeft("") { (res, p) => res + p._1 + "=" + p._2 + " " })
      println()
      val mean = Stats.mean(results)
      val stdev = Stats.stdDev(mean, results)
      println("Average time: " + mean)
      println("Stddev time: " + ("%.6f" format stdev))

      val iterresults = results.map { iters / _ }
      val itermean = Stats.mean(iterresults)
      val iterstdev = Stats.stdDev(itermean, iterresults)
      println("Average iters/sec: " + ("%.0f" format itermean))
      println("Stddev iters/sec: " + ("%.2f" format iterstdev))

      println("--------------- Individual running times -------------------")
      results foreach { println _ }
      println("------------------------------------------------------------")
    }
  }

  private class Terminator extends Thread {
    setDaemon(true)
    override def run() {
      System.in.read()
      println("Terminating by user intervention...")
      System.exit(0)
    }
  }
}