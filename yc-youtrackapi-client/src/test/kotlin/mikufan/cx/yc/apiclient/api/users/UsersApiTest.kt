package mikufan.cx.yc.apiclient.api.users

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.yc.apiclient.config.ApiServiceProvider
import mikufan.cx.yc.apiclient.util.ENABLE_BY_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_URI
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZoneId

@OptIn(ExperimentalKotest::class)
@SpringBootTest
class UsersApiTest(
  apiServiceProvider: ApiServiceProvider,
) : ShouldSpec({

  val usersApi = apiServiceProvider.usersApi(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN)
  context("for me").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
    should("get zone id") {
      val zoneId = usersApi.getZoneIdOfUser()
      zoneId shouldBe ZoneId.of("America/Toronto")
    }
  }
})
