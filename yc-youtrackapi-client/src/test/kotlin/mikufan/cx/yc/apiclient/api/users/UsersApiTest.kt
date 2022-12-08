package mikufan.cx.yc.apiclient.api.users

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.yc.apiclient.YouTrackApiClientAutoConfiguration
import mikufan.cx.yc.apiclient.config.ApiServiceAutoConfiguration
import mikufan.cx.yc.apiclient.config.WebClientAutoConfiguration
import mikufan.cx.yc.apiclient.config.YouTrackApiAuthInfo
import mikufan.cx.yc.apiclient.util.ENABLE_BY_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_URI
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.web.reactive.function.client.WebClient
import java.time.ZoneId

@OptIn(ExperimentalKotest::class)
class UsersApiTest : ShouldSpec({
  val applicationContextRunner = ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(YouTrackApiClientAutoConfiguration::class.java))
  context("for me").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
    should("get zone id") {
      applicationContextRunner
        .withBean(
          YouTrackApiAuthInfo::class.java,
          { YouTrackApiAuthInfo(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN) }
        )
        .run { ctx ->
//          ctx.beanDefinitionNames.forEach { println(it) }
          assertThat(ctx).hasSingleBean(WebClientAutoConfiguration::class.java)
          assertThat(ctx).hasSingleBean(WebClient::class.java)
          assertThat(ctx).hasSingleBean(ApiServiceAutoConfiguration::class.java)
          assertThat(ctx).hasSingleBean(UsersApi::class.java)
          val usersApi = ctx.getBean<UsersApi>()
          val zoneId = usersApi.getZoneIdOfUser()
          zoneId shouldBe ZoneId.of("America/Toronto")
        }
    }
  }
})
