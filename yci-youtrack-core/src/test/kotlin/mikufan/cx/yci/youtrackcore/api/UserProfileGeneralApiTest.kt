package mikufan.cx.yci.youtrackcore.api

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.matchers.shouldBe
import mikufan.cx.yci.youtrackcore.model.BaseYouTrackApiRequest
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI
import java.time.ZoneId

@OptIn(ExperimentalKotest::class)
@SpringBootTest
class UserProfileGeneralApiTest(
  private val userProfileGeneralApi: UserProfileGeneralApi,
) : ShouldSpec({
  val enableByToken: EnabledOrReasonIf = {
    if (System.getenv("YOUTRACK_BEARER_TOKEN") != null) Enabled.enabled else Enabled.disabled("Skip tests that require YOUTRACK_BEARER_TOKEN")
  }
  context("for me").config(enabledOrReasonIf = enableByToken) {
    should("get zone id") {
      val zoneId = userProfileGeneralApi.getZoneIdOfUser(
        youTrackApiRequest = BaseYouTrackApiRequest(
          URI("https://youtrack.jetbrains.com/api/"),
          System.getenv("YOUTRACK_BEARER_TOKEN")
        )
      )
      zoneId shouldBe ZoneId.of("America/Toronto")
    }
  }
})
