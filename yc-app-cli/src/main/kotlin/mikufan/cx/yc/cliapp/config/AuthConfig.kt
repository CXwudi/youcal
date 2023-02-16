package mikufan.cx.yc.cliapp.config

import jakarta.validation.constraints.NotBlank
import mikufan.cx.yc.apiclient.yt.autoconfig.YouTrackApiAuthInfo
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.net.URI

/**
 * @author CX无敌
 * 2023-01-01
 */
@ConfigurationProperties(prefix = "cliapp.auth")
@Validated
data class AuthConfigRaw(
  /** the base url with the base uri path of the youtrack instance */
  @field:URL
  val baseUri: String,
  /** the bearerToken to log in to the youtrack instance */
  @field:NotBlank
  val bearerToken: String,
)

@Configuration
class AuthConfigConfiguration {

  @Bean
  fun authConfig(raw: AuthConfigRaw): YouTrackApiAuthInfo = YouTrackApiAuthInfo(
    baseURI = URI(raw.baseUri),
    bearerToken = raw.bearerToken,
  )
}
