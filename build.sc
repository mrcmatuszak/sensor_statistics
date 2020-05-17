// build.sc
import mill._, scalalib._,  scalafmt._

object app extends ScalaModule with ScalafmtModule {
     def scalaVersion = "2.13.1"

      object test extends Tests {
          def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.7.1")
          def testFrameworks = Seq("utest.runner.Framework")
     }
}