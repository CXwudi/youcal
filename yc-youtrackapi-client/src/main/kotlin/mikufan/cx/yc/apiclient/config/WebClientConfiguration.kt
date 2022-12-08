package mikufan.cx.yc.apiclient.config

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author CX无敌
 * 2022-11-29
 */
@AutoConfiguration
@ConditionalOnBean(YouTrackApiAuthInfo::class)
class WebClientConfiguration(
  private val wcBuilder: WebClient.Builder,
) {

  /**
   * Get a web client for youtrack api
   * @param baseURI the base uri of the youtrack instance
   * @param bearerToken the bearer token of the youtrack user
   * @return a web client for youtrack api
   */
  @Bean
  fun getWebClient(authInfo: YouTrackApiAuthInfo): WebClient {
    log.debug { "Created new client based on ${authInfo.baseURI}" }
    return wcBuilder.baseUrl(authInfo.baseURI.toString())
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${authInfo.bearerToken}")
      .build()
  }
}

private val log = KInlineLogging.logger()
