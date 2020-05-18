// build.sc
import mill._, scalalib._,  scalafmt._

object app extends SbtModule with ScalafmtModule {
     def scalaVersion = "2.13.1"
     def scoverageVersion = "1.4.0"

     override def scalacOptions = Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:higherKinds"
  )

     def ivyDeps = Agg(
          ivy"co.fs2::fs2-core:2.2.1",
          ivy"co.fs2::fs2-io:2.2.1"
     )

      object test extends Tests with ScalafmtModule {
          def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.7.1")
          def testFrameworks = Seq("utest.runner.Framework")
     }
}