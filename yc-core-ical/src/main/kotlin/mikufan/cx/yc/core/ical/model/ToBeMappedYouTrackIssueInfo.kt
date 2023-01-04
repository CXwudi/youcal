package mikufan.cx.yc.core.ical.model

import mikufan.cx.yc.core.ical.util.YouTrackIssueJson

/**
 * @author CX无敌
 * 2023-01-02
 */

sealed interface ToBeMappedYouTrackIssueInfo {
  /**
   * this field just to show the one on one mapping to [EventType]
   *
   * doesn't really have any functionality
   */
  val eventType: EventType
  val json: YouTrackIssueJson
  val dateTimeFieldInfo: DateTimeFieldInfo

  /**
   *  null = alarm disabled
   */
  val alarmSetting: AlarmSetting?
  val otherMappings: OtherStringMappings
}

data class OneDayIssueInfo(
  override val json: YouTrackIssueJson,
  override val dateTimeFieldInfo: OneDayIssueDateTimeFieldInfo,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: OtherStringMappings,
) : ToBeMappedYouTrackIssueInfo {
  override val eventType: EventType = EventType.ONE_DAY_EVENT
}

data class DurationDateTimeIssueInfo(
  override val json: YouTrackIssueJson,
  override val dateTimeFieldInfo: DurationDateTimeIssueDateTimeFieldInfo,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: OtherStringMappings,
) : ToBeMappedYouTrackIssueInfo {
  override val eventType: EventType = EventType.DURATION_DATETIME_EVENT
}
