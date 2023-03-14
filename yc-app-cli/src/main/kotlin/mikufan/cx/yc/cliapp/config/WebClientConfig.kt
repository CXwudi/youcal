package mikufan.cx.yc.cliapp.config

import io.netty.handler.logging.LogLevel
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactorNettyHttpClientMapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

/**
 * @author CX无敌
 * 2023-01-04
 */
@ConfigurationProperties(prefix = "cliapp.webclient")
class WebClientConfig(
  /**
   * Do you want to check what does Spring Web 6 Declarative HTTP Client is really doing?
   *
   * Then enable this, but be careful, it will also log your bearer token
   */
  val debug: Boolean = false,
  /** The user-agent, not really matter for YouTrack */
  val userAgent: String = "YouCal @ https://github.com/CXwudi/youcal",
)

@Component
class UserAgentCustomizer(
  config: WebClientConfig,
) : WebClientCustomizer {
  private val userAgent = config.userAgent

  override fun customize(builder: WebClient.Builder) {
    if (userAgent.isNotBlank()) {
      builder.defaultHeader(HttpHeaders.USER_AGENT, userAgent)
    }
  }
}

@Component
class DebugEnablingCustomizer(
  config: WebClientConfig,
) : ReactorNettyHttpClientMapper {
  private val debugEnabled = config.debug
  override fun configure(httpClient: HttpClient): HttpClient {
    return if (debugEnabled) {
      httpClient.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
    } else {
      httpClient
    }
  }
}

/**
 * This class is a workaround for GraalVM native image build
 *
 * Mentioned from https://blog.csdn.net/ssehs/article/details/125463972
 */
@Component
class AddressResolverGroupCustomizer : ReactorNettyHttpClientMapper {
  override fun configure(httpClient: HttpClient): HttpClient {
    return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE)
  }
}
