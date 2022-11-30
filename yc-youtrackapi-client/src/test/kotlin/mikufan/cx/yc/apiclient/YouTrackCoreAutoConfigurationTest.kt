package mikufan.cx.yc.apiclient

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class YouTrackCoreAutoConfigurationTest : ShouldSpec({
  should("config correctly") {
    true shouldBe true
  }
})
