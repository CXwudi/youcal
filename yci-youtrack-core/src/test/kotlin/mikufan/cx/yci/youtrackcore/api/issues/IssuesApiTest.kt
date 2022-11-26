package mikufan.cx.yci.youtrackcore.api.issues

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.verify
import mikufan.cx.yci.youtrackcore.model.issues.IssuesGetRequest
import mikufan.cx.yci.youtrackcore.util.ENABLE_BY_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_URI
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest
class IssuesApiTest(
  @field:SpykBean private val restTemplate: RestTemplate,
  private val issuesApi: IssuesApi,
) : ShouldSpec({
  val sampleRequest = IssuesGetRequest(
    baseUri = YOUTRACK_TEST_URI,
    bearerToken = YOUTRACK_TEST_BEARER_TOKEN,
    query = "project: {Toolbox App}",
    fields = listOf("idReadable", "summary", "description"),
    customFields = listOf("Due Day", "customField2"),
    pageSize = 40
  )
  val sampleUri =
    URI("https://youtrack.jetbrains.com/api/issues?query=project:%20%7BToolbox%20App%7D&fields=idReadable,summary,description,customFields(name,value(name))&customFields=Due%20Day&customFields=customField2")
  context("url building") {
    should("correctly build base url without pagination") {
      issuesApi.buildBaseQueryUrl(sampleRequest) shouldBe sampleUri
    }
  }

  context("calling the API") {
    should("correctly called pagination url").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
      val arrayNode = issuesApi.doApiCall(sampleUri, YOUTRACK_TEST_BEARER_TOKEN, 40, 0)
      arrayNode[0].shouldBeInstanceOf<ObjectNode>()
      arrayNode.size() shouldBe 40
    }

    should("lazily fetch issues list").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
      val iterator = issuesApi.readIssueListLazily(sampleRequest)
      iterator.hasNext() shouldBe true
      iterator.next().shouldBeInstanceOf<ObjectNode>()
      verify(exactly = 1) { restTemplate.exchange(any(), object : ParameterizedTypeReference<ArrayNode>() {}) }
    }
  }
})
