package mikufan.cx.yc.icalcore

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ICalCoreAutoConfigurationTest : ShouldSpec({
  should("boot") {
    1 shouldBe 1
  }
})
