package my.mixin

/**
 * require kotlin-mixin and spring-boot-mixin
 */
plugins {
  id("my.root.jvm")
  kotlin("plugin.spring")
}

configurations {
  all {
    exclude(group = "org.mockito")
    exclude(group = "org.mockito.kotlin")
  }
}

dependencies {
  implementation("com.github.CXwudi:kotlin-jvm-inline-logging")
  testImplementation("io.kotest.extensions:kotest-extensions-spring")
  testImplementation("io.mockk:mockk")
  testImplementation("com.ninja-squad:springmockk")
  // due to https://github.com/Ninja-Squad/springmockk#limitations, mockk and springmockk are not added by default here,
  // use my.spring-boot-mockk-mixin to add mockk instead
}
