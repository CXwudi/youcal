package mikufan.cx.yc.cliapp.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.apiclient.yt.api.users.UsersApi
import mikufan.cx.yc.cliapp.config.validation.TimeZoneFormat
import mikufan.cx.yc.core.ical.model.EventDateTimeFieldName
import mikufan.cx.yc.core.ical.model.EventType
import mikufan.cx.yc.core.ical.model.OneFieldName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.time.Duration
import java.time.ZoneId

/**
 * @author CX无敌
 * 2023-01-01
 */

@ConfigurationProperties(prefix = "cliapp.datetime")
@Validated
data class DateTimeConfigRaw(
  /** map each YouTrack issue to what kind of VEvent, support any type defined in [EventType]*/
  @get:NotNull
  val eventType: EventType,
  /** name of the datetime related fields to be used to extract the needed datetime data */
  @get:NotEmpty
  val fieldNames: List<@NotBlank String>,
  /**
   * be used in all kinds of [EventType] except [EventType.ONE_DAY_EVENT]
   * be used when the second field is not provided or blank value from the second field
   */
  val defaultDuration: Duration?,
  /** any additional parameters for the chosen [eventType], currently unused */
  val additionalParameters: Map<String, String> = emptyMap(),
  /** the zone id of all YouTrack issues, default to the zone id of the current bearer token */
  @get:TimeZoneFormat
  val zoneId: String?,
)

@Configuration
class DateTimeConfigConfiguration {

  @Bean
  fun dateTimeConfig(raw: DateTimeConfigRaw, usersApi: UsersApi): DateTimeConfig {
    // TODO: more logic when supporting more event types
    return DateTimeConfig(
      eventType = raw.eventType,
      fieldNames = OneFieldName(raw.fieldNames[0]),
      defaultDuration = raw.defaultDuration,
      zoneIdProvider = {
        if (raw.zoneId.isNullOrBlank()) {
          log.info { "Calling API to get the zone id of the current bearer token" }
          usersApi.getZoneIdOfUser()
        } else {
          ZoneId.of(raw.zoneId)
        }
      },
    )
  }
}

class DateTimeConfig(
  val eventType: EventType,
  val fieldNames: EventDateTimeFieldName,
  val defaultDuration: Duration? = null,
  zoneIdProvider: () -> ZoneId,
) {
  val zoneId: ZoneId by lazy(zoneIdProvider)
}

private val log = KInlineLogging.logger()
