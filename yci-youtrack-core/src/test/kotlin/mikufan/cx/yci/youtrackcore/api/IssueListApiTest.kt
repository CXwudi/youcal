package mikufan.cx.yci.youtrackcore.api

import com.fasterxml.jackson.databind.node.ObjectNode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import mikufan.cx.yci.youtrackcore.model.IssueListRequest
import mikufan.cx.yci.youtrackcore.util.ENABLE_BY_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yci.youtrackcore.util.YOUTRACK_TEST_URI
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI

@SpringBootTest
class IssueListApiTest(
  private val issueListApi: IssueListApi,
) : ShouldSpec({
  context("url building") {
    val expectedUri =
      URI("https://youtrack.jetbrains.com/api/issues?query=project:%20%7BToolbox%20App%7D&fields=idReadable,summary,description,customFields(name,value(name))&customFields=Due%20Day&customFields=customField2")
    should("correctly build base url without pagination") {
      val sampleRequest = IssueListRequest(
        baseUri = YOUTRACK_TEST_URI,
        bearerToken = YOUTRACK_TEST_BEARER_TOKEN,
        query = "project: {Toolbox App}",
        fields = listOf("idReadable", "summary", "description"),
        customFields = listOf("Due Day", "customField2"),
        pageSize = 40
      )
      issueListApi.buildBaseQueryUrl(sampleRequest) shouldBe expectedUri
    }

    should("correctly called pagination url").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
      val arrayNode = issueListApi.doApiCall(expectedUri, YOUTRACK_TEST_BEARER_TOKEN, 40, 0)
      arrayNode[0].shouldBeInstanceOf<ObjectNode>()
      arrayNode.size() shouldBe 40
    }
  }
})
