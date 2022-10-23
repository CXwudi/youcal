package mikufan.cx.yci.icalcore

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ICalCoreSpringBootAutoConfigurationTest : ShouldSpec({
  should("boot") {
    1 shouldBe 1
  }
})
