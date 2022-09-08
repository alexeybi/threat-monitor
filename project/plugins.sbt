addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.10.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"             % "2.0.0-RC1")
addSbtPlugin("com.github.sbt"     % "sbt-native-packager"      % "1.9.11")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"      % "0.21.0")

libraryDependencies += "net.exoego" %% "scalajs-env-jsdom-nodejs" % "2.1.0"
