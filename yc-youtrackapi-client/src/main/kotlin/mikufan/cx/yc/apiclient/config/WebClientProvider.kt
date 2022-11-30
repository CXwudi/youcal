package mikufan.cx.yc.apiclient.config

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

/**
 * @author CX无敌
 * 2022-11-29
 */
@Component
@EnableCaching
class WebClientProvider(
  private val wcBuilder: WebClient.Builder,
) {

  @Cacheable("youtrackApiClient")
  fun getWebClient(baseURI: URI, bearerToken: String): WebClient {
    log.debug { "Created new client based on $baseURI and $bearerToken" }
    return wcBuilder.baseUrl(baseURI.toString())
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $bearerToken")
      .build()
  }
}

private val log = KInlineLogging.logger()
