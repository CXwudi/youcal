package mikufan.cx.yc.cliapp.config

import io.netty.handler.logging.LogLevel
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
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
class WebClientBuilderCustomizer(
  private val config: WebClientConfig,
) : WebClientCustomizer {
  override fun customize(builder: WebClient.Builder) {
    if (config.debug) {
      val httpClient: HttpClient = HttpClient.create()
        .wiretap(this.javaClass.canonicalName, LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
      val conn: ClientHttpConnector = ReactorClientHttpConnector(httpClient)
      builder.clientConnector(conn)
    }
    if (config.userAgent.isNotBlank()) {
      builder.defaultHeader(HttpHeaders.USER_AGENT, config.userAgent)
    }
  }
} 