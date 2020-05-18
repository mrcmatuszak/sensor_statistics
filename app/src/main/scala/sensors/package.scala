package com.luxoft
package object sensors {

  // Type alias used for differentiate sensor
  type Sensor = String

  // Type alias representing state for given sensor
  // Keeps track of sensor and its accumulative state
  type SensorState = Map[Sensor, SensorObservation]

}
