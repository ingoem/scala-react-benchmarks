package react.bench

import java.io.InputStreamReader
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.BufferedReader
import java.io.IOException
import collection.JavaConversions
import java.lang.{ Process => JProcess }
import collection.mutable.HashSet
import collection.mutable.SynchronizedSet

class ProcessTools {
  private val runningProcesses = new HashSet[Process] with SynchronizedSet[Process]
  @volatile private var stopping = false

  object ShutdownThread extends Thread {
    override def run() {
      stopping = true
      runningProcesses foreach { p =>
        if (p.hasStarted && !p.hasTerminated) {
          println("Terminating process " + p.name)
          p.terminate()
          println("Terminated.")
        }
      }
    }
  }

  Runtime.getRuntime.addShutdownHook(ShutdownThread)

  def startProcess(cmd: Array[String], out: OutputStream = null, redirectErr: Boolean = true): Process = {
    val builder = new ProcessBuilder(cmd: _*)
    builder.redirectErrorStream(redirectErr)
    val p = new Process(builder, cmd.mkString(" "))

    if(out != null) pipe(p.inputStream, out)
    p
  }

  final class Process private[ProcessTools] (builder: ProcessBuilder, val name: String) { process =>
    private val jprocess: JProcess = if(stopping) null else {
      runningProcesses += this
      builder.start()
    }
    private val waiter = {
      val w = new Waiter
      w.start()
      w
    }

    def hasStarted = jprocess != null
    def hasTerminated = waiter.done
    def terminate() = jprocess.destroy()
    def exitValue = jprocess.exitValue

    def waitFor() = jprocess.waitFor()

    def errorStream = jprocess.getErrorStream
    def inputStream = jprocess.getInputStream
    def outputStream = jprocess.getOutputStream

    private class Waiter extends Thread {
      @volatile var done = false

      def hasTerminated = done

      override def run() {
        try {
          if(jprocess != null) jprocess.waitFor()
        } finally {
          done = true
          runningProcesses -= process
        }
      }
    }

    def pipe(opPerLine: String=>Unit) {
      val in = new BufferedReader(new InputStreamReader(inputStream))
      var s = ""
      while(s != null) {
        s = in.readLine()
        if(s != null) opPerLine(s)
      }
    }
  }

  private def pipe(in: InputStream, out: OutputStream): Thread = {
    val t = new In2OutPiper(in, out)
    t.start()
    t
  }

  private class In2OutPiper(in: InputStream, out: OutputStream) extends Thread {
    override def run() {
      try {
        val reader = new BufferedInputStream(in)
        val data = new Array[Byte](1024)
        var len = reader.read(data)
        while (len > -1) {
          out.write(data, 0, len)
          len = reader.read(data)
        }
        reader.close()
      } catch {
        case e: IOException => e.printStackTrace()
      }
    }
  }
}