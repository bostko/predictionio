/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import PIOBuild._

lazy val scalaSparkDepsVersion = Map(
  "2.11" -> Map(
    "2.0" -> Map(
      "akka" -> "2.5.16",
      "hadoop" -> "2.7.7",
      "json4s" -> "3.2.11"),
    "2.1" -> Map(
      "akka" -> "2.5.17",
      "hadoop" -> "2.7.7",
      "json4s" -> "3.2.11"),
    "2.2" -> Map(
      "akka" -> "2.5.17",
      "hadoop" -> "2.7.7",
      "json4s" -> "3.2.11"),
    "2.3" -> Map(
      "akka" -> "2.5.17",
      "hadoop" -> "2.7.7",
      "json4s" -> "3.2.11")),
  "2.12" -> Map(
    "2.4" -> Map(
      "akka" -> "2.4.20",
      "hadoop" -> "2.7.7",
      "json4s" -> "3.5.3")))

name := "apache-predictionio-parent"

version in ThisBuild := "0.16.0-SNAPSHOT"

organization in ThisBuild := "org.apache.predictionio"

scalaVersion in ThisBuild := sys.props.getOrElse("scala.version", "2.12.10")

scalaTestVersion in ThisBuild := sys.props.getOrElse("scalatest.version", "3.0.3")

scalaBinaryVersion in ThisBuild := binaryVersion(scalaVersion.value)

crossScalaVersions in ThisBuild := Seq("2.12.10")

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked", "-feature")

scalacOptions in (ThisBuild, Test) ++= Seq("-Yrangepos")
fork in (ThisBuild, run) := true

javacOptions in (ThisBuild, compile) ++= Seq("-source", "1.8", "-target", "1.8",
  "-Xlint:deprecation", "-Xlint:unchecked")

// Ignore differentiation of Spark patch levels
sparkVersion in ThisBuild := sys.props.getOrElse("spark.version", "2.4.7")

sparkBinaryVersion in ThisBuild := binaryVersion(sparkVersion.value)

hadoopVersion in ThisBuild := sys.props.getOrElse("hadoop.version", "2.7.7")

akkaVersion in ThisBuild := sys.props.getOrElse("akka.version", "2.4.20")

elasticsearchVersion in ThisBuild := sys.props.getOrElse("elasticsearch.version", "5.6.9")

hbaseVersion in ThisBuild := sys.props.getOrElse("hbase.version", "2.1.7")

json4sVersion in ThisBuild := {
  sparkBinaryVersion.value match {
    case "2.0" | "2.1" | "2.2" | "2.3" => "3.2.11"
    case "2.4" => "3.5.3"
  }
}

val conf = file("conf")

val commonSettings = Seq(
  autoAPIMappings := true,
  licenseConfigurations := Set("compile"),
  licenseReportTypes := Seq(Csv),
  unmanagedClasspath in Test += conf,
  unmanagedClasspath in Test += baseDirectory.value.getParentFile / s"storage/jdbc/target/scala-${scalaBinaryVersion.value}/classes")

val commonTestSettings = Seq(
  libraryDependencies ++= Seq(
    "org.postgresql"   % "postgresql"  % "42.2.9" % "test",
    "org.scalikejdbc" %% "scalikejdbc" % "3.2.3" % "test"))

val dataHdfs = (project in file("storage/hdfs")).
  settings(commonSettings: _*)

val dataJdbc = (project in file("storage/jdbc")).
  settings(commonSettings: _*)

val dataLocalfs = (project in file("storage/localfs")).
  settings(commonSettings: _*)

//val dataS3 = (project in file("storage/s3")).
//  settings(commonSettings: _*).

val common = (project in file("common")).
  settings(commonSettings: _*).
  disablePlugins(sbtassembly.AssemblyPlugin)

val data = (project in file("data")).
  dependsOn(common).
  settings(commonSettings: _*).
  settings(commonTestSettings: _*).
  disablePlugins(sbtassembly.AssemblyPlugin)

val core = (project in file("core")).
  dependsOn(data).
  settings(commonSettings: _*).
  settings(commonTestSettings: _*).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      scalaBinaryVersion,
      sbtVersion,
      sparkVersion,
      hadoopVersion),
    buildInfoPackage := "org.apache.predictionio.core"
  ).
  enablePlugins(SbtTwirl).
  disablePlugins(sbtassembly.AssemblyPlugin)

val e2 = (project in file("e2")).
  dependsOn(core).
  settings(commonSettings: _*).
  disablePlugins(sbtassembly.AssemblyPlugin)

val tools = (project in file("tools")).
  dependsOn(e2).
  settings(commonSettings: _*).
  settings(commonTestSettings: _*).
  settings(skip in publish := true).
  enablePlugins(SbtTwirl)

val storageProjectReference = Seq(
//    dataElasticsearch,
//    dataHbase,
    dataHdfs,
    dataJdbc,
    dataLocalfs,
//    dataS3
) map Project.projectToRef

val storage = (project in file("storage"))
  .settings(skip in publish := true)
  .aggregate(storageProjectReference: _*)
  .disablePlugins(sbtassembly.AssemblyPlugin)

val assembly = (project in file("assembly")).
  settings(commonSettings: _*)

val root = (project in file(".")).
  settings(commonSettings: _*).
  aggregate(common, core, data, tools, e2).
  disablePlugins(sbtassembly.AssemblyPlugin)

homepage := Some(url("http://predictionio.apache.org"))

pomExtra := {
  <parent>
    <groupId>org.apache</groupId>
    <artifactId>apache</artifactId>
    <version>18</version>
  </parent>
  <scm>
    <connection>scm:git:github.com/apache/predictionio</connection>
    <developerConnection>scm:git:https://gitbox.apache.org/repos/asf/predictionio.git</developerConnection>
    <url>github.com/apache/predictionio</url>
  </scm>
  <developers>
    <developer>
      <id>donald</id>
      <name>Donald Szeto</name>
      <url>http://predictionio.apache.org</url>
      <email>donald@apache.org</email>
    </developer>
  </developers>
}

childrenPomExtra in ThisBuild := {
  <parent>
    <groupId>{organization.value}</groupId>
    <artifactId>{name.value}_{scalaBinaryVersion.value}</artifactId>
    <version>{version.value}</version>
  </parent>
}

concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.CPU, 1),
  Tags.limit(Tags.Network, 1),
  Tags.limit(Tags.Test, 1),
  Tags.limitAll( 1 )
)

parallelExecution := false

parallelExecution in Global := false

testOptions in Test += Tests.Argument("-oDF")

printBuildInfo := {
  println(s"PIO_SCALA_VERSION=${scalaVersion.value}")
  println(s"PIO_SPARK_VERSION=${sparkVersion.value}")
  println(s"PIO_HADOOP_VERSION=${hadoopVersion.value}")
  println(s"PIO_ELASTICSEARCH_VERSION=${elasticsearchVersion.value}")
  println(s"PIO_HBASE_VERSION=${hbaseVersion.value}")
}
