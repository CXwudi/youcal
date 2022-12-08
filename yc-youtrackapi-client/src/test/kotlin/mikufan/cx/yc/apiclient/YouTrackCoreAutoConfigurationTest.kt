package mikufan.cx.yc.apiclient

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.apiclient.api.issues.IssuesApi
import mikufan.cx.yc.apiclient.config.YouTrackApiAuthInfo
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_URI
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import java.util.function.Supplier

class YouTrackCoreAutoConfigurationTest : ShouldSpec({
  context("an annotation config app ctx") {
    val ctx = runApplication<YouTrackApiClientAutoConfiguration>() {
      addInitializers(AddConfigInitializer())
    } as GenericApplicationContext
    should("auto config") {
//      ctx.beanDefinitionNames.forEach { println(it) }
      shouldNotThrow<NoSuchBeanDefinitionException> { ctx.getBean<IssuesApi>() }
    }
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
