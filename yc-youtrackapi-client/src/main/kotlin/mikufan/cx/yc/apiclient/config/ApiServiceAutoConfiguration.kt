package mikufan.cx.yc.apiclient.config

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.apiclient.api.issues.IssuesApi
import mikufan.cx.yc.apiclient.api.users.UsersApi
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.Duration

/**
 * @author CX无敌
 * 2022-11-29
 */
@AutoConfiguration(after = [WebClientAutoConfiguration::class])
@ConditionalOnBean(WebClient::class)
class ApiServiceAutoConfiguration(
//  @Autowired(required = false)
  private val wc: WebClient,
) {

  @Bean
  @ConditionalOnMissingBean
  fun issuesApi(factory: HttpServiceProxyFactory): IssuesApi = factory.createApiService()

  @Bean
  @ConditionalOnMissingBean
  fun usersApi(factory: HttpServiceProxyFactory): UsersApi = factory.createApiService()

  @Bean
  @ConditionalOnMissingBean
  fun httpServiceProxyFactory(): HttpServiceProxyFactory {
    val webClientAdapter = WebClientAdapter.forClient(wc)
    return HttpServiceProxyFactory.builder(webClientAdapter)
      .blockTimeout(Duration.ofSeconds(10))
      .build()
  }

  private inline fun <reified T> HttpServiceProxyFactory.createApiService(): T {
    log.debug { "Created the ${T::class} api" }
    return this.createClient(T::class.java)
  }
}

private val log = KInlineLogging.logger()
