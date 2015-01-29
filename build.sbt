import AssemblyKeys._

organization  := "com.example"

version       := "0.3"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.8"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
    "io.spray"            %%  "spray-json"    % "1.2.6",
    "com.gu"              %%  "prequel"       % "0.3.12",
    "org.scalaz"          %%  "scalaz-core"   % "7.0.6",
    "org.postgresql"      %   "postgresql"    % "9.3-1101-jdbc41",
    "com.codahale.metrics" % "metrics-healthchecks" % "3.0.2",
    "joda-time"           % "joda-time"       % "2.3",
    "com.google.guava"    % "guava"           % "17.0",
    "com.google.code.findbugs" % "jsr305" % "1.3.9",
    "org.bouncycastle" % "bcprov-ext-jdk14" % "1.51",
    "org.bouncycastle" % "bcpkix-jdk14" % "1.51"
  )
}

javaOptions += "-XX:MaxPermSize=1024"

Revolver.settings

assemblySettings

