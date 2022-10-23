package mikufan.cx.yci.youtrackcore.api

import com.fasterxml.jackson.databind.node.ObjectNode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import mikufan.cx.yci.youtrackcore.model.IssueListRequest
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI

@SpringBootTest
class YouTrackIssueListApiTest(
  private val youTrackIssueListApi: YouTrackIssueListApi,
) : ShouldSpec({
  context("url building") {
    val expectedUri =
      URI("https://youtrack.jetbrains.com/api/issues?query=project:%20%7BToolbox%20App%7D&fields=idReadable,summary,description,customFields(name,value(name))&customFields=Due%20Day&customFields=customField2")
    should("correctly build base url without pagination") {
      val sampleRequest = IssueListRequest(
        baseUri = URI("https://youtrack.jetbrains.com/api/"),
        bearerToken = "123",
        query = "project: {Toolbox App}",
        fields = listOf("idReadable", "summary", "description"),
        customFields = listOf("Due Day", "customField2"),
        pageSize = 40
      )
      youTrackIssueListApi.buildBaseQueryUrl(sampleRequest) shouldBe expectedUri
    }

    xshould("correctly called pagination url") {
      val arrayNode = youTrackIssueListApi.doApiCall(expectedUri, "", 40, 0)
      arrayNode[0].shouldBeInstanceOf<ObjectNode>()
      arrayNode.size() shouldBe 40
    }
  }
})
