package com.luxoft
package sensors

import Reading._
import java.nio.file.Paths

import utest._

object StatisticsTest extends utest.TestSuite {
  val tests = Tests {
    test("Sensor state") {
      test("From empty") {

        val empty = SensorObservation.Empty

        val out = SensorsOps.updateObservation(empty, ValidReading(Paths.get("file1.csv"), "s1", 10))

        out ==> SensorObservation(0, 1, 10, 10, 10)
      }

      test("Add valid reading (zero)") {

        val ss  = SensorObservation(0, 1, 10, 0, 10)
        val out = SensorsOps.updateObservation(ss, ValidReading(Paths.get("file2.csv"), "s1", 0))

        out ==> SensorObservation(0, 2, 10, 0, 10)
      }

      test("Add valid reading") {

        val ss  = SensorObservation(0, 2, 10, 0, 10)
        val out = SensorsOps.updateObservation(ss, ValidReading(Paths.get("file2.csv"), "s1", 11))

        out ==> SensorObservation(0, 3, 21, 0, 11)
      }

      test("Add invalid reading") {
        val ss  = SensorObservation(0, 3, 21, 0, 11)
        val avg = SensorsOps.updateObservation(ss, InvalidReading(Paths.get("file2.csv"), "s1"))

        avg ==> SensorObservation(1, 3, 21, 0, 11)
      }
    }
    test("Create summary") {
      test("From empty accumulator") {
        val empty = Accumulator.Empty

        val summary = empty.makeSummary()

        summary ==> SensorsSummary(0, 0, 0, Seq.empty)

      }
      test("From non-empty accumulator") {

        val acc = Accumulator
          .Empty
          .accumulate(ValidReading(Paths.get("test1"), "s1", 100))
          .accumulate(ValidReading(Paths.get("test1"), "s1", 50))
          .accumulate(InvalidReading(Paths.get("test2"), "s2"))

        val summary = acc.makeSummary()

        summary ==> SensorsSummary(2, 3, 1, Seq(("s1", Some(ObservationSummary(50, 100, 75.0))), ("s2", None)))

      }
    }
  }
}
