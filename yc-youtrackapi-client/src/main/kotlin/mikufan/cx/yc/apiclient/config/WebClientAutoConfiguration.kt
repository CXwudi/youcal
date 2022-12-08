package mikufan.cx.yc.apiclient.config

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author CX无敌
 * 2022-11-29
 */
@AutoConfiguration(after = [WebClientAutoConfiguration::class])
@ConditionalOnBean(YouTrackApiAuthInfo::class, WebClient.Builder::class)
class WebClientAutoConfiguration(
  private val wcBuilder: WebClient.Builder,
) {

  /**
   * Get a web client for youtrack api
   * @param authInfo the auth info for youtrack api
   * @return a web client for youtrack api
   */
  @Bean
  @ConditionalOnMissingBean
  fun youtrackWebClient(authInfo: YouTrackApiAuthInfo): WebClient {
    log.debug { "Created new client based on ${authInfo.baseURI}" }
    return wcBuilder.baseUrl(authInfo.baseURI.toString())
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${authInfo.bearerToken}")
      .build()
  }
}

private val log = KInlineLogging.logger()
