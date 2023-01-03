@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("my.kotlin-spring-app")
  id("my.app-mixin") // to generate a run script version of this app
  id("my.spring-boot-mockk-mixin")
  alias(libs.plugins.graalvm.native.image)
}

dependencies {
  implementation(project(":yc-core-ical"))
  implementation(project(":yc-apiclient-youtrack"))
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.mnode.ical4j:ical4j")
}

application {
  mainClass.set("mikufan.cx.yc.cliapp.MainKt")
}
