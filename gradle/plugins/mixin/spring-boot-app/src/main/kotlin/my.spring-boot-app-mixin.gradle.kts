import my.mixin.springboot.MySpringBootAppExtension
import my.mixin.springboot.MySpringBootLoggingFramework
import org.springframework.boot.gradle.tasks.bundling.BootJar

/**
 * requires spring-boot-mixin
 */
plugins {
  id("my.jvm-root")
  // every spring app project must apply this plugin, this plugin is not just simply wrap a boot jar,
  // but also configure kotlin and Java compiler compilation parameters to make json or reflection work correctly.
  // see https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.java.
  // although we could add these parameters manually in jvm-root and kotlin-jvm-mixin
  id("org.springframework.boot")
}

// taken from https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#packaging-executable.and-plain-archives
tasks.named<BootJar>("bootJar") {
  archiveClassifier.set("boot")
}

tasks.named<Jar>("jar") {
  archiveClassifier.set("")
}

// this extension should only be used in the spring boot app
extensions.create<MySpringBootAppExtension>("mySpringBootApp")

afterEvaluate {
  val ext = this.extensions.getByType(MySpringBootAppExtension::class.java)
  if (ext.loggingFramework.get() == MySpringBootLoggingFramework.LOG4J2) {
    // we must use the configuration excluding method because the spring lib is still using logback,
    // and it makes sense that the user would only want to change the logging framework on the app side
    // so that all spring lib modules can use whatever the spring app wants to use.
    this.configurations {
      all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
      }
    }
    this.dependencies {
      implementation("org.springframework.boot:spring-boot-starter-log4j2")
    }
//    this.dependencies {
//      implementation("org.springframework.boot:spring-boot-starter-log4j2")
//      modules {
//        module("org.springframework.boot:spring-boot-starter-logging") { // instead of excluding, we can replace one module to another
//          replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
//        }
//      }
//    }
  }
}
