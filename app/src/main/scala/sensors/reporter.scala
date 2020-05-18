package com.luxoft
package sensors

case class ObservationSummary(min: Long, max: Long, avg: Double)
case class SensorsSummary(
    sourceCount:         Long,
    observations:        Long,
    failedObservations:  Long,
    observationsSummary: Seq[(Sensor, Option[ObservationSummary])]
)

object SensorsSummary {
  val Empty = SensorsSummary(0, 0, 0, Seq.empty)
}

class Reporter(acc: Accumulator) {

  def makeReport(): String = {

    val summary = acc.makeSummary()

    makeReport(summary)

  }

  private def makeReport(summary: SensorsSummary): String =
    s"""
Num of processed files: ${summary.sourceCount}
Num of processed measurements: ${summary.observations}
Num of failed measurements: ${summary.failedObservations}

Sensors with highest avg humidity:

sensor-id,min,avg,max
${summary.observationsSummary
         .sortBy {
           case (_, Some(v)) => v.avg
           case (_, None)    => -1
         }(Ordering.Double.TotalOrdering.reverse)
         .map {
           case (sensor, Some(obs)) => s"$sensor,${obs.min},${obs.avg},${obs.max}"
           case (sensor, None)      => s"$sensor,N/A"
         }
         .mkString("\n")}

    """.stripMargin

}
