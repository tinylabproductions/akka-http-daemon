## akka-http-daemon

A quick and easy way to launch akka-http based http server daemon.

### Usage

**For akka 2.4 and akka-http 10.0 use version 1.3.0 of this library.**

Otherwise, add to your build.sbt:

```
resolvers += Resolver.bintrayRepo("tinylabproductions", "maven")
libraryDependencies ++= Seq(
  "com.tinylabproductions" %% "akka-http-daemon" % "1.4.0",
  // This depends on akka >= 2.5 & akka-http >= 10.1
  // See: https://doc.akka.io/docs/akka-http/current/release-notes/10.1.x.html#akka-is-not-an-explicit-dependency-anymore-removal-of-akka-2-4-support
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.typesafe.akka" %% "akka-http" % "10.1.2",
)
```

You might want to check out [bintray repository](https://bintray.com/tinylabproductions/maven/akka-http-daemon) as well.