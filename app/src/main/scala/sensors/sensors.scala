package com.luxoft
package sensors

import java.nio.file.Path
import Reading._

final case class SensorObservation(
    invalidCount: Long,
    validCount:   Long,
    sumValue:     Long,
    minValue:     Long,
    maxValue:     Long
) {
  def isValid:    Boolean = validCount > 0
  def totalCount: Long    = invalidCount + validCount
}

object SensorObservation {
  val Empty = SensorObservation(0, 0, 0, Long.MaxValue, Long.MinValue)
}

trait SensorsOps {

  def updateObservation(sensors: SensorObservation, reading: Reading): SensorObservation = reading match {

    case InvalidReading(path, sensor) => sensors.copy(invalidCount = sensors.invalidCount + 1)

    case ValidReading(path, sensor, value) => {
      val min = Math.min(sensors.minValue, value)
      val max = Math.max(sensors.maxValue, value)

      sensors
        .copy(validCount = sensors.validCount + 1)
        .copy(sumValue = sensors.sumValue + value)
        .copy(minValue = min)
        .copy(maxValue = max)

    }
  }
}

object SensorsOps extends SensorsOps

final case class Accumulator(private val paths: Set[Path], private val ref: Map[Sensor, SensorObservation]) {

  def accumulate(reading: Reading): Accumulator = {

    val so = ref.getOrElse(reading.sensor, SensorObservation.Empty)

    val updated = SensorsOps.updateObservation(so, reading)

    new Accumulator(paths + reading.path, ref + (reading.sensor -> updated))
  }

  def makeSummary(): SensorsSummary =
    ref.foldLeft(SensorsSummary.Empty) {
      case (acc, (sensor, observation)) =>
        val os: Option[ObservationSummary] = if (observation.isValid) {
          Some(
            ObservationSummary(observation.minValue,
                               observation.maxValue,
                               observation.sumValue / observation.validCount)
          )
        } else {
          None
        }

        acc
          .copy(sourceCount = paths.size)
          .copy(observations = acc.observations + observation.totalCount)
          .copy(failedObservations = acc.failedObservations + observation.invalidCount)
          .copy(observationsSummary = acc.observationsSummary :+ (sensor, os))
    }

}

object Accumulator {

  def apply(): Accumulator = new Accumulator(Set.empty, Map.empty)

  val Empty = Accumulator()
}
