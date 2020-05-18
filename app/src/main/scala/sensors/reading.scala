package com.luxoft
package sensors

import java.nio.file.Path

sealed trait Reading {
  def path:   Path
  def sensor: String
}

object Reading {
  final case class ValidReading(path:   Path, sensor: String, value: Int) extends Reading
  final case class InvalidReading(path: Path, sensor: String) extends Reading
}

trait ReadingOps {
  import Reading._

  /**
    * Parses single line
    * Expected format "sensor-1,10"
    * TODO: Handle incorrect format
    *
    * @param path
    * @param line
    * @return
    */
  def parseLine(path: Path, line: String): Reading = {

    val s = line.split(",")

    val sensor = s(0).trim

    try {
      val reading = s(1).trim.toInt
      ValidReading(path, sensor, reading)
    } catch {
      case nfe: java.lang.NumberFormatException => InvalidReading(path, sensor)
    }
  }
}
