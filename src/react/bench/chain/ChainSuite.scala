package react.bench
package chain

object ChainSuite extends BenchmarkSuite {
  val tests = for(depth <- Seq(50, 100);
      bench <- Seq(
          //TestDef(ObChain)("depth" -> depth),
          TestDef(EventChain)("depth" -> depth),
          TestDef(FlowEventChain)("depth" -> depth),
          TestDef(SignalChain)("depth" -> depth, "strict" -> false),
          TestDef(SignalChain)("depth" -> depth, "strict" -> true)
      )) yield bench
}