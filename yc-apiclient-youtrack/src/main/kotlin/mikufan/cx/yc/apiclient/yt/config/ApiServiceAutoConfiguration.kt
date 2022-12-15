package mikufan.cx.yc.apiclient.yt.config

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.apiclient.yt.api.issues.IssuesApi
import mikufan.cx.yc.apiclient.yt.api.users.UsersApi
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.web.service.invoker.HttpServiceProxyFactory

/**
 * @author CX无敌
 * 2022-11-29
 */
@AutoConfiguration(after = [WebClientAutoConfiguration::class])
@ConditionalOnBean(HttpServiceProxyFactory::class)
class ApiServiceAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun issuesApi(factory: HttpServiceProxyFactory): IssuesApi = factory.createApiService()

  @Bean
  @ConditionalOnMissingBean
  fun usersApi(factory: HttpServiceProxyFactory): UsersApi = factory.createApiService()

  private inline fun <reified T> HttpServiceProxyFactory.createApiService(): T {
    log.debug { "Created the ${T::class} api" }
    return this.createClient(T::class.java)
  }
}

private val log = KInlineLogging.logger()
