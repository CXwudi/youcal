package mikufan.cx.yc.cliapp.component

import mikufan.cx.yc.cliapp.config.AlarmConfig
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.cliapp.config.DisabledAlarmConfig
import mikufan.cx.yc.cliapp.config.EnabledAlarmConfig
import mikufan.cx.yc.core.ical.model.*
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import org.springframework.stereotype.Component

/**
 * @author CX无敌
 * 2023-01-04
 */
@Component
class ToBeMappedYouTrackIssueInfoCreator(
  private val dateTimeConfig: DateTimeConfig,
  private val alarmConfig: AlarmConfig,
  private val otherStringMappings: OtherStringMappings,
) {

  fun createInfo(
    json: YouTrackIssueJson,
  ): ToBeMappedYouTrackIssueInfo {
    val alarmSetting = createAlarmSetting(alarmConfig)
    return when (dateTimeConfig.eventType) {
      EventType.ONE_DAY_EVENT -> {
        val dateTimeFieldInfo = OneDayIssueDateTimeFieldInfo(
          fieldName = dateTimeConfig.fieldNames[0],
          zoneId = dateTimeConfig.zoneId,
        )
        OneDayIssueInfo(
          json = json,
          dateTimeFieldInfo = dateTimeFieldInfo,
          alarmSetting = alarmSetting,
          otherMappings = otherStringMappings,
        )
      }

//      EventType.DURATION_DATETIME_EVENT -> {
//        val dateTimeFieldInfo = DurationDateTimeIssueDateTimeFieldInfo(
//          startFieldName = dateTimeConfig.fieldNames[0],
//          durationFieldName = dateTimeConfig.fieldNames[1],
//          defaultDuration = dateTimeConfig.defaultDuration,
//        )
//        DurationDateTimeIssueInfo(
//          json = json,
//          dateTimeFieldInfo = dateTimeFieldInfo,
//          alarmSetting = alarmSetting,
//          otherMappings = otherStringMappings,
//        )
//      }
    }
  }

  private fun createAlarmSetting(alarmConfig: AlarmConfig): AlarmSetting? {
    return when (alarmConfig) {
      is DisabledAlarmConfig -> null
      is EnabledAlarmConfig -> AlarmSetting(
        durationFieldName = alarmConfig.periodFieldName,
        isNegativeDurationField = alarmConfig.isNegativePeriod,
        defaultShiftDuration = alarmConfig.defaultDuration,
        shiftBasedOn = alarmConfig.shiftBasedOn,
      )
    }
  }
}
