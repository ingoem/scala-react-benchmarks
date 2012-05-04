package react.bench
package fanout

object FanoutSuite extends BenchmarkSuite {
  val tests = for(fanout <- Seq(10, 50, 100);
      bench <- Seq(
          TestDef(ObFanout)("fanout" -> fanout),
          TestDef(EventFanout)("fanout" -> fanout),
          TestDef(FlowEventFanout)("fanout" -> fanout),
          TestDef(SignalFanout)("fanout" -> fanout, "strict" -> false),
          TestDef(SignalFanout)("fanout" -> fanout, "strict" -> true)
      )) yield bench
}