val scala3Version = "3.8.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sql-template",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    libraryDependencies += "org.postgresql" % "postgresql" % "42.7.11"
  )
