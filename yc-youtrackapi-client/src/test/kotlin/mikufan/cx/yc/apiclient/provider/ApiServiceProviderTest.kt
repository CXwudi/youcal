package mikufan.cx.yc.apiclient.provider

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import mikufan.cx.yc.apiclient.api.issues.IssuesApi
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_BEARER_TOKEN
import mikufan.cx.yc.apiclient.util.YOUTRACK_TEST_URI
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.EnableCaching
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@EnableCaching
class ApiServiceProviderTest(
  private val webClientProvider: WebClientProvider,
  private val apiServiceProvider: ApiServiceProvider,
) : ShouldSpec({
  context("get api service") {
    should("get api service by method") {
      apiServiceProvider.issuesApi(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN).shouldBeInstanceOf<IssuesApi>()
      apiServiceProvider.issuesApi(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN).shouldBeInstanceOf<IssuesApi>()
      webClientProvider.getWebClient(YOUTRACK_TEST_URI, "123").shouldBeInstanceOf<WebClient>()
      apiServiceProvider.issuesApi(YOUTRACK_TEST_URI, "123").shouldBeInstanceOf<IssuesApi>()
    }
  }
})
