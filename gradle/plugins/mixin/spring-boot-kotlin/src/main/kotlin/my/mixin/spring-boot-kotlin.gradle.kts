package my.mixin

/**
 * require kotlin-mixin and spring-boot-mixin
 */
plugins {
  id("my.root.jvm")
  kotlin("plugin.spring")
}

fun DependencyHandlerScope.sbs(dep: String? = null, version: String? = null) =
  "org.springframework.boot:spring-boot-starter${dep?.let { "-$it" } ?: ""}${version?.let { ":$it" } ?: ""}"

dependencies {
  versionConstraints(platform("org.springframework.boot:spring-boot-dependencies"))
  implementation(sbs())
  // both spring lib and app potentially need this.
  // for spring app, adding this gives intellij auto-complete for application.yml
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  // with spring boot, you are pretty much fixed to use spring-boot-starter-test, which uses junit 5,
  // so don't bother extracting spring-boot-starter-test to a separate mixin...
  implementation("com.github.CXwudi:kotlin-jvm-inline-logging")
  testImplementation(sbs("test")) {
    exclude(group = "org.mockito")
    exclude(group = "org.mockito.kotlin")
  }
  testImplementation("io.kotest.extensions:kotest-extensions-spring")
  testImplementation("io.mockk:mockk")
  testImplementation("com.ninja-squad:springmockk")
  // due to https://github.com/Ninja-Squad/springmockk#limitations, mockk and springmockk are not added by default here,
  // use my.spring-boot-mockk-mixin to add mockk instead
}
