pluginManagement {
  repositories {
    gradlePluginPortal()
    // google()
  }
  includeBuild("gradle/plugins") // use this to include our own convention plugins
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    // google()
    maven("https://jitpack.io")
  }

  includeBuild("gradle/platform") // use this to include our own gradle platform to centralize version management
  // gradle/libs.versions.toml is automatically imported if exists
}

plugins {
  id("com.gradle.enterprise") version "3.12.1"
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "YouCal"

rootDir.listFiles { file -> file.isDirectory && !file.isHidden && file.name.startsWith("yc") }?.forEach {
  include(it.name)
}
