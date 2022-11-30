package mikufan.cx.yc.apiclient.provider

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

/**
 * A provider that can geenrate a web client for youtrack api with predefined settings
 * @author CX无敌
 * 2022-11-29
 */
@Component
class WebClientProvider(
  private val wcBuilder: WebClient.Builder,
) {

  /**
   * Get a web client for youtrack api
   * @param baseURI the base uri of the youtrack instance
   * @param bearerToken the bearer token of the youtrack user
   * @return a web client for youtrack api
   */
  @Cacheable("youtrackApiClient")
  fun getWebClient(baseURI: URI, bearerToken: String): WebClient {
    log.debug { "Created new client based on $baseURI" }
    return wcBuilder.baseUrl(baseURI.toString())
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $bearerToken")
      .build()
  }
}

private val log = KInlineLogging.logger()
