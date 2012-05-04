package react.bench

object Stats {
  def stdDev(mean: Double, values: Seq[Double]): Double = {
    val variance = values.foldLeft(0d) { (acc, x) => acc + (x - mean) * (x - mean) } / values.length.toDouble
    math.sqrt(variance);
  }

  def mean(results: Seq[Double]): Double = {
    val sum = results.foldLeft(0d) { _ + _ }
    sum / results.length
  }
}