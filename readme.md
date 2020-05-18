# Design Decisions

I decided to use FS2 library for streaming. 

Processing is done in following steps:

- reads all paths from given directory, ignoring subdirectories and non-csv files
- reads files by line in chunks and create stream of it
- converts each line into [Reading](./app/src/main/scala/sensors/reading.scala). Reading is a trait with two subclasses representing [ValidReading](./app/src/main/scala/sensors/reading.scala) and [InvalidReading](./app/src/main/scala/sensors/reading.scala)
- in next step, stream is 'folded' into [Accumulator](./app/src/main/scala/sensors/sensors.scala) which holds information about state of read data. It keeps track of origin paths and mapping between sensor nad sensor's observation. [SensorObservation](./app/src/main/scala/sensors/sensors.scala) is simple case class with count of invalid/valid reads, total sum for calculating average and global min/max for given sensor.

  Using accumulator makes stream space efficient because it depends only on number of sensors and not the number of files or measurements
- last step creates Report based on accumulated raw statistics

# Running

Application is using mill build tool because it fast and efficient for small project like this (I added sbt for compatibility).

To compile code use
```
  ./mill app.compile
```

To run all tests use

```
  ./mill app.test
```

Application expects path to directory holding csv files as first argument 

```
  ./mill app.run /path/to/csv/

```



## Generate test data

Use [./generate.sh](./generate.sh) to generate sample date

```
# ./generate.sh #number_of_sensors #sample_count #lower_bound #upper_bound
# Example 
./generate.sh 100 100000 0 100 > /tmp/sample1.txt

```

# Performance

Processing 5 files with 201053125 rows (231MB each) takes 60 seconds.

# Improvements

Although application is fulfilling requirements and can handle any number of arbitrary size files it can be still improve:
- add better command line handling - at the moment it expects path to directory holding csv file
- improve error handling, application will explode when line parsing fails due to different reason than conversion exception 
- maybe go more functional: use lenses for updating state, define algebras and Tagless Final for encoding behavior



# Sensor Statistics Task

Create a command line program that calculates statistics from humidity sensor data.

### Background story

The sensors are in a network, and they are divided into groups. Each sensor submits its data to its group leader.
Each leader produces a daily report file for a group. The network periodically re-balances itself, so the sensors could
change the group assignment over time, and their measurements can be reported by different leaders. The program should
help spot sensors with highest average humidity.

## Input

- Program takes one argument: a path to directory
- Directory contains many CSV files (*.csv), each with a daily report from one group leader
- Format of the file: 1 header line + many lines with measurements
- Measurement line has sensor id and the humidity value
- Humidity value is integer in range `[0, 100]` or `NaN` (failed measurement)
- The measurements for the same sensor id can be in the different files

### Example

leader-1.csv
```
sensor-id,humidity
s1,10
s2,88
s1,NaN
```

leader-2.csv
```
sensor-id,humidity
s2,80
s3,NaN
s2,78
s1,98
```

## Expected Output

- Program prints statistics to StdOut
- It reports how many files it processed
- It reports how many measurements it processed
- It reports how many measurements failed
- For each sensor it calculates min/avg/max humidity
- `NaN` values are ignored from min/avg/max
- Sensors with only `NaN` measurements have min/avg/max as `NaN/NaN/NaN`
- Program sorts sensors by highest avg humidity (`NaN` values go last)

### Example

```
Num of processed files: 2
Num of processed measurements: 7
Num of failed measurements: 2

Sensors with highest avg humidity:

sensor-id,min,avg,max
s2,78,82,88
s1,10,54,98
s3,NaN,NaN,NaN
```

## Notes

- Single daily report file can be very large, and can exceed program memory
- Program should only use memory for its internal state (no disk, no database)
- Any open source library can be used (besides Spark) 
- Please use vanilla scala, akka-stream, monix or similar technology. 
- You're more than welcome to implement a purely functional solution using cats-effect, fs2 and/or ZIO to impress, 
  but this is not a mandatory requirement. 
- Sensible tests are welcome
