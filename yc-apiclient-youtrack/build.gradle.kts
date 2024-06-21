@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("my.kotlin-spring-lib")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

kotlin {
  compilerOptions {
    freeCompilerArgs = freeCompilerArgs.get() + "-Xjvm-default=all"
  }
}
