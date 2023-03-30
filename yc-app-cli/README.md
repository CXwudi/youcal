# YouCal CLI App

This CLI App simply takes
a [search query](https://www.postman.com/youtrack-dev/workspace/youtrack/request/24356758-fa9a9439-98b5-463d-a954-9e235d5ed40d)
of YouTrack issues and some settings you can tune, and transform all issues into `VEvent` and export them to a `.ics`
file.

The app is written using Spring Boot, and heavily relies on Spring Boot Configuration to receive user input.

## Build

run `./gradlew assemble :yc-app-cli` at the root directory (not this current directory) to build the app.

Or run `./gradlew nativeCompile :yc-app-cli` at the root directory
(not this current directory) to build a native image if you have setup GraalVM with `native-image`.

## Usage

This is a CLI application, but all configs are done via Spring Boot Configuration.

So please be familiar with Spring
Boot's [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

Some good ways to use externalized configuration are Environment Variables,
Importing additional properties/YAML file, Passing command line, etc.

All configuration properties are prefixed with `cliapp.` and can be found
in [`application.yml`](src/main/resources/application.yml).

Unfortunately, I couldn't make the auto-generated configuration properties metadata work,
so there is no documentation on `application.yml`.
Instead, documentation is written on `@ConfigurationProperties` classes, see you would need to look at source codes
in [`mikufan.cx.yc.cliapp.config` package](src/main/kotlin/mikufan/cx/yc/cliapp/config) to check the documentation.

A sample integration test showing one possible usage of most of the configuration properties can be found
at [`Acceptance1Test`](src/test/kotlin/mikufan/cx/yc/cliapp/MainAcceptanceTest.kt)
