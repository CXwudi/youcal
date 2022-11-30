package mikufan.cx.yci.youtrackcore.api.issues

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yci.youtrackcore.config.ApiServiceProvider
import mikufan.cx.yci.youtrackcore.util.ENABLE_BY_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_URI
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author CX无敌
 * 2022-11-29
 */
@OptIn(ExperimentalKotest::class)
@SpringBootTest
class IssuesApiTest(
  apiServiceProvider: ApiServiceProvider,
) : ShouldSpec({

  context("api client").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
    val issuesApi = apiServiceProvider.issuesApi(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN)
    should("get issues list") {
      val iterator = issuesApi.getIssuesLazily(
        "project: {Toolbox App}",
        listOf("idReadable", "summary", "description", "customFields(name,id,value(name,id))"),
        listOf("OS", "Assignee", "State", "Priority", "Type", "Fix versions"),
        5,
      )
      iterator.hasNext() shouldBe true
      repeat(5) {
        log.info { "issue = ${iterator.next()}" }
      }
    }
  }
})

private val log = KInlineLogging.logger()
