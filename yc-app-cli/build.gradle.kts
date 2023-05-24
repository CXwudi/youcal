@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("my.kotlin-spring-app")
  id("my.mixin.app") // to generate a run script version of this app
  alias(libs.plugins.graalvm.native.image)
}

dependencies {
  implementation(project(":yc-core-ical"))
  implementation(project(":yc-apiclient-youtrack"))
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation(libs.dep.ical4j)
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(
      group = "com.vaadin.external.google",
      module = "android-json",
    )
  }
}

application {
  mainClass.set("mikufan.cx.yc.cliapp.MainKt")
}
