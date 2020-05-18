val app = project
    .settings(
        name := "sensor_statistics",
        version := "1.0.0",
        scalaVersion := "2.13.1",
        scalacOptions := Seq(
            "-deprecation",
            "-encoding",
            "utf-8",
            "-explaintypes",
            "-feature",
            "-language:existentials",
            "-language:higherKinds"),
        testFrameworks += new TestFramework("utest.runner.Framework"),
        libraryDependencies ++= {
            val fs2Version = "2.2.1"
             Seq(
                "co.fs2" %% "fs2-core" % fs2Version,
                "co.fs2" %% "fs2-io" % fs2Version,
                "com.lihaoyi" %% "utest" % "0.7.1" % "test"
            )
        }
    )