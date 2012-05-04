package react.bench
package drag

object DragSuite extends BenchmarkSuite {
  val tests = for(take <- Seq(10, 50, 100);
      excess <- Seq(10, 100, 1000);
      bench <- Seq(
          TestDef(ObDragging)("take" -> take, "excess" -> excess),
          TestDef(FRPFixedDragging)("take" -> take, "excess" -> excess),
          TestDef(FlowDragging)("take" -> take, "excess" -> excess)
      )) yield bench
}