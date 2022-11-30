package mikufan.cx.yci.youtrackcore.config

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yci.youtrackcore.api.issues.IssuesApi
import mikufan.cx.yci.youtrackcore.api.users.UsersApi
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.net.URI
import java.time.Duration

/**
 * @author CX无敌
 * 2022-11-29
 */
@Component
class ApiServiceProvider(
  private val wcProvider: WebClientProvider,
) {

  @Cacheable("issuesApi")
  fun issuesApi(baseURI: URI, bearerToken: String): IssuesApi = createApiService(baseURI, bearerToken)

  @Cacheable("usersApi")
  fun usersApi(baseURI: URI, bearerToken: String): UsersApi = createApiService(baseURI, bearerToken)

  private inline fun <reified T> createApiService(baseURI: URI, bearerToken: String): T {
    log.debug { "Created a new ${T::class} api by $baseURI and $bearerToken" }
    val wc = wcProvider.getWebClient(baseURI, bearerToken)

    val webClientAdapter = WebClientAdapter.forClient(wc)
    val httpServiceProxyFactory = HttpServiceProxyFactory.builder(webClientAdapter)
      .blockTimeout(Duration.ofSeconds(10))
      .build()
    return httpServiceProxyFactory.createClient(T::class.java)
  }
}

private val log = KInlineLogging.logger()
