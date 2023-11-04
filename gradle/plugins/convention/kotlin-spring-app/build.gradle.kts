plugins {
  `kotlin-dsl` // !id("kotlin-dsl"), this tells gradle to use kotlin dsl
}

dependencies {
  implementation(project(":mixin:kotlin-jvm"))
  // let makes all spring apps using app mixin to enjoy the better Jar packaging
  implementation(project(":mixin:app"))
  implementation(project(":mixin:spring-boot-kotlin"))
  implementation(project(":mixin:spring-boot-app"))
  // we could merge the spring-boot-app and app mixins into here
  // but unsure if in the future we'll need to separate them again
  // (e.g., cloud native app without Spring Boot)
}
