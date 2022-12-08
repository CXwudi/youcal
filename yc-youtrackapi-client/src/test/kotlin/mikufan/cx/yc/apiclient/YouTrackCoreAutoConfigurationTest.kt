package mikufan.cx.yc.apiclient

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class YouTrackCoreAutoConfigurationTest(
  private val ctx: ApplicationContext
) : ShouldSpec({
  should("config correctly") {
    ctx.beanDefinitionNames.forEach { println(it) }
    true shouldBe true
  }
})
