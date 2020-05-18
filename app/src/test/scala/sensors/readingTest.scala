package com.luxoft
package sensors

import utest._
import java.nio.file.Paths

object ReadingOpsTest extends utest.TestSuite with ReadingOps {
  val tests = Tests {
    test("Parse input") {
      test("valid reading") {
        val r = parseLine(Paths.get("file1.txt"), "s1,10")
      }
      test("invalid reading") {
        val r = parseLine(Paths.get("file1.txt"), "s1,N/A")
      }
    }
  }
}
