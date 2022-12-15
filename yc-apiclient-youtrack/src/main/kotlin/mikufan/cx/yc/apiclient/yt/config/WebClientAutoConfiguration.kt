package mikufan.cx.yc.apiclient.yt.config

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.Duration

/**
 * @author CX无敌
 * 2022-11-29
 */
@AutoConfiguration(after = [WebClientAutoConfiguration::class])
class WebClientAutoConfiguration {

  /**
   * Get a web client for youtrack api
   * @param authInfo the auth info for youtrack api
   * @return a web client for youtrack api
   */
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(YouTrackApiAuthInfo::class, WebClient.Builder::class)
  fun youtrackWebClient(authInfo: YouTrackApiAuthInfo, wcBuilder: WebClient.Builder): WebClient {
    log.debug { "Created new client based on ${authInfo.baseURI}" }
    return wcBuilder.baseUrl(authInfo.baseURI.toString())
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${authInfo.bearerToken}")
      .build()
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(WebClient::class)
  fun httpServiceProxyFactory(wc: WebClient): HttpServiceProxyFactory {
    val webClientAdapter = WebClientAdapter.forClient(wc)
    return HttpServiceProxyFactory.builder(webClientAdapter)
      .blockTimeout(Duration.ofSeconds(10))
      .build()
  }
}

private val log = KInlineLogging.logger()
