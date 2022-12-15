package mikufan.cx.yc.apiclient.api.issues

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.apiclient.YouTrackApiClientAutoConfiguration
import mikufan.cx.yc.apiclient.config.ApiServiceAutoConfiguration
import mikufan.cx.yc.apiclient.config.WebClientAutoConfiguration
import mikufan.cx.yc.apiclient.config.YouTrackApiAuthInfo
import mikufan.cx.yc.apiclient.util.ENABLE_BY_TOKEN
import mikufan.cx.yc.apiclient.util.SpringBootTestWithTestProfile
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_URI
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author CX无敌
 * 2022-11-29
 */
@OptIn(ExperimentalKotest::class)
@SpringBootTestWithTestProfile
class IssuesApiTest : ShouldSpec({
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
          Assertions.assertThat(ctx).hasSingleBean(WebClientAutoConfiguration::class.java)
          Assertions.assertThat(ctx).hasSingleBean(WebClient::class.java)
          Assertions.assertThat(ctx).hasSingleBean(ApiServiceAutoConfiguration::class.java)
          Assertions.assertThat(ctx).hasSingleBean(IssuesApi::class.java)
          val issuesApi = ctx.getBean<IssuesApi>()
          val iterator = issuesApi.getIssuesLazily(
            "project: {Toolbox App}",
            listOf("idReadable", "summary", "description", "customFields(name,id,value(name,id))"),
            listOf("OS", "Assignee", "State", "Priority", "Type", "Fix versions"),
            5,
            5
          )
          iterator.hasNext() shouldBe true
          repeat(5) {
            log.info { "issue = ${iterator.next()}" }
          }
        }
    }
  }
})

private val log = KInlineLogging.logger()
