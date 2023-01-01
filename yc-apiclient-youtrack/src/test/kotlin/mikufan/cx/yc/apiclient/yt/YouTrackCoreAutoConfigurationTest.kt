package mikufan.cx.yc.apiclient.yt

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.yc.apiclient.yt.api.issues.IssuesApi
import mikufan.cx.yc.apiclient.yt.autoconfig.ApiServiceAutoConfiguration
import mikufan.cx.yc.apiclient.yt.autoconfig.WebClientAutoConfiguration
import mikufan.cx.yc.apiclient.yt.autoconfig.YouTrackApiAuthInfo
import mikufan.cx.yc.apiclient.yt.util.YOUTRACK_TEST_URI
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.GenericApplicationContext
import java.util.function.Supplier

// @SpringBootTestWithTestProfile
class YouTrackCoreAutoConfigurationTest : ShouldSpec({
  context("a runApplication app ctx") {
    val ctx = runApplication<YouTrackApiClientAutoConfiguration>() {
      addInitializers(AddConfigInitializer())
    } as GenericApplicationContext
    should("auto config") {
//      ctx.beanDefinitionNames.forEach { println(it) }
      shouldNotThrow<NoSuchBeanDefinitionException> { ctx.getBean<IssuesApi>() }
    }
    println(ctx.environment.getProperty("spring.main.web-application-type"))
    xshould("set non web type") {
      ctx.environment.getProperty("spring.main.web-application-type") shouldBe "none"
    }
  }

  xcontext("a annotation app ctx") {
    val ctx =
      AnnotationConfigApplicationContext(YouTrackApiClientAutoConfiguration::class.java)
    ctx.registerBean(
      YouTrackApiAuthInfo::class.java,
      Supplier {
        YouTrackApiAuthInfo(
          YOUTRACK_TEST_URI,
          "admin",
        )
      }
    )
    ctx.registerBean(
      WebClientAutoConfiguration::class.java,
      Supplier {
        WebClientAutoConfiguration()
      }
    )
    ctx.registerBean(
      ApiServiceAutoConfiguration::class.java,
      Supplier {
        ApiServiceAutoConfiguration()
      }
    )
    ctx.beanDefinitionNames.forEach { println(it) }
  }
})

class AddConfigInitializer : ApplicationContextInitializer<GenericApplicationContext> {
  override fun initialize(applicationContext: GenericApplicationContext) {
    applicationContext.registerBean(
      YouTrackApiAuthInfo::class.java,
      Supplier {
        YouTrackApiAuthInfo(YOUTRACK_TEST_URI, "random token")
      }
    )
  }
}
