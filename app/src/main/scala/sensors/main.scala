package com.luxoft

package sensors

import fs2._
import scala.util.Random
import java.nio.file.{ Files, Path, Paths }
import cats.effect.{ Blocker, IO }
import cats.effect.IOApp
import cats.effect.ExitCode
import cats.effect.ContextShift
import scala.concurrent.ExecutionContext

object Main extends FilesystemSupport with ArgsSupport with ReadingOps {

  implicit val ctxShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def main(args: Array[String]): Unit = {

    val path = parseArgs(args)

    val files = listFiles(Paths.get(path))

    processor(files).compile.toList.unsafeRunSync.foreach(println)

  }

  /**
    *
    * Calculates statistics from humidity sensor data
    *
    * @param files
    * @return
    */
  def processor(files: Seq[Path]): Stream[IO, String] =
    Stream
      .resource(Blocker[IO])
      .flatMap(blocker => Stream.emits(files).flatMap(p => read(p, blocker)))
      .fold(Accumulator.Empty)(_ accumulate _)
      .map(acc => new Reporter(acc).makeReport())

  /**
    * Reads single file in chunks (of size 4096) into stream
    *
    * @param path
    * @param blocker
    * @return
    */
  def read(path: Path, blocker: Blocker): Stream[IO, Reading] =
    io.file
      .readAll[IO](path, blocker, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .drop(1)
      .filter(!_.isEmpty)
      .map(l => parseLine(path, l))
}

trait FilesystemSupport {
  import scala.jdk.CollectionConverters._

  /**
    * Lists csv files from given directory
    *
    * @param path
    * @return
    */
  def listFiles(path: Path): Seq[Path] =
    Files.walk(path).iterator().asScala.filter(Files.isRegularFile(_)).filter(_.toString.endsWith(".csv")).toList
}

trait ArgsSupport {

  def parseArgs(args: Array[String]): String =
    args.headOption.fold(throw new IllegalArgumentException("Please provide path to sensor data. Exiting ☠️"))(identity)
}
