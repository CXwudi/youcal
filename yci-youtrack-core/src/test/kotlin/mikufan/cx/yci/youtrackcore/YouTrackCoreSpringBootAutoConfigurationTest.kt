package mikufan.cx.yci.youtrackcore

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class YouTrackCoreSpringBootAutoConfigurationTest : ShouldSpec({
  should("config correctly") {
    true shouldBe true
  }
})
