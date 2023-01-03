package mikufan.cx.yc.core.ical.model

import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-02
 */

sealed interface ToBeMappedYouTrackIssueInfo {
  val eventType: EventType
  val json: ObjectNode

  /**
   *  alarm can be disabled, non-null means it is enabled.
   */
  val alarmSetting: AlarmSetting?
  val otherMappings: OtherStringMappings
}

data class OneDayIssueInfo(
  override val json: ObjectNode,
  val fieldName: String,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: OtherStringMappings,
) : ToBeMappedYouTrackIssueInfo {
  override val eventType: EventType = EventType.ONE_DAY_EVENT
}

data class DurationDatetimeIssueInfo(
  override val json: ObjectNode,
  val startFieldName: String,
  val durationFieldName: String?,
  val defaultDuration: Duration,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: OtherStringMappings,
) : ToBeMappedYouTrackIssueInfo {
  override val eventType: EventType = EventType.DURATION_DATETIME_EVENT
}
