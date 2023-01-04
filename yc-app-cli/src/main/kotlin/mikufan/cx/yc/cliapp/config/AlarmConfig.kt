package mikufan.cx.yc.cliapp.config

import mikufan.cx.yc.cliapp.config.validation.IsValidAlarmConfig
import mikufan.cx.yc.core.ical.model.ShiftBasedOn
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-04
 */
@ConfigurationProperties(prefix = "cliapp.alarm")
@Validated
@IsValidAlarmConfig
data class AlarmConfigRaw(
  val enabled: Boolean = true,
  /** Using which YouTrack Issue Period(duration) field to create the alarm */
  val periodFieldName: String? = null,
  /**
   * Whether the period field should be traded as before or after the related date field.
   *
   * By default this is `true`, which means alarm is created before the related date field.
   */
  val isNegativePeriod: Boolean = true,
  /**
   * The default duration, which can be positive or negative.
   *
   * This will be used whenever no period field specified, or the value in the period field is invalid
   */
  val defaultDuration: Duration? = null,
  /**
   * Is it the start datetime or end datetime that the alarm is created related to.
   *
   * By default, this is [ShiftBasedOn.START]
   */
  val shiftBasedOn: ShiftBasedOn = ShiftBasedOn.START,
)

@Configuration
class AlarmConfigConfiguration {

  @Bean
  fun alarmConfig(raw: AlarmConfigRaw): AlarmConfig = when (raw.enabled) {
    true -> EnabledAlarmConfig(
      periodFieldName = raw.periodFieldName ?: "",
      isNegativePeriod = raw.isNegativePeriod,
      defaultDuration = raw.defaultDuration,
      shiftBasedOn = raw.shiftBasedOn,
    )

    false -> DisabledAlarmConfig
  }
}

sealed interface AlarmConfig {
  val enabled: Boolean
}

data class EnabledAlarmConfig(
  val periodFieldName: String = "",
  val isNegativePeriod: Boolean = true,
  val defaultDuration: Duration?,
  val shiftBasedOn: ShiftBasedOn,
) : AlarmConfig {
  override val enabled: Boolean = true
}

object DisabledAlarmConfig : AlarmConfig {
  override val enabled: Boolean = false
}
