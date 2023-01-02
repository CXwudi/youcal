package mikufan.cx.yc.cliapp.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.apiclient.yt.api.users.UsersApi
import mikufan.cx.yc.core.ical.model.DateTimeFieldType
import mikufan.cx.yc.core.ical.model.EventDateTimeField
import mikufan.cx.yc.core.ical.model.EventType
import mikufan.cx.yc.core.ical.model.OneEventField
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
  /** The field name of the start date of the event */
  @get:Size(min = 1, max = 1, message = "Currently only support one date time field")
  val fields: List<@NotBlank String>,
  /** Is it a datetime or a date */
  @get:NotNull
  val fieldType: DateTimeFieldType,
  /** if one date field + datetime type, the duration of the event, in minute, default to 10 minutes */
  val defaultDatetimeDuration: Duration?,
  /** the zone id of all YouTrack issues, default to the zone id of the current bearer token */
  val zoneId: ZoneId?,
)

@Configuration
class DateTimeConfigConfiguration {

  @Bean
  fun dateTimeConfig(raw: DateTimeConfigRaw, usersApi: UsersApi): DateTimeConfig = DateTimeConfig(
    eventType = when (raw.fieldType) {
      DateTimeFieldType.DATE -> EventType.ONE_DAY_EVENT
      DateTimeFieldType.DATETIME -> EventType.DURATION_DATE_TIME_EVENT
    },
    fields = OneEventField(raw.fields[0]),
    defaultDuration = when (raw.fieldType) {
      DateTimeFieldType.DATE -> null
      DateTimeFieldType.DATETIME -> requireNotNull(raw.defaultDatetimeDuration) {
        "defaultDatetimeDuration must be provided when fieldType is DATETIME"
      }
    },
    zoneIdProvider = {
      raw.zoneId ?: let {
        log.info { "Determining the zone id of the current bearer token by calling the API" }
        usersApi.getZoneIdOfUser()
      }
    }
  )
}

class DateTimeConfig(
  val eventType: EventType,
  val fields: EventDateTimeField,
  val defaultDuration: Duration? = null,
  zoneIdProvider: () -> ZoneId,
) {
  val zoneId: ZoneId by lazy(zoneIdProvider)
}

private val log = KInlineLogging.logger()
