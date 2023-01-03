package mikufan.cx.yc.core.ical.model

import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-02
 */

sealed interface ToBeMappedYouTrackIssueInfo {
  val json: ObjectNode

  /**
   *  alarm can be disabled, non-null means it is enabled and the program should make sure it is a valid setting
   */
  val alarmSetting: AlarmSetting?
  val otherMappings: List<OtherStringMapping>
}

data class OneDayIssueInfo(
  override val json: ObjectNode,
  val fieldName: String,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: List<OtherStringMapping>,
) : ToBeMappedYouTrackIssueInfo

data class DurationDatetimeIssueInfo(
  override val json: ObjectNode,
  val startFieldName: String,
  val durationFieldName: String?,
  val defaultDuration: Duration?,
  override val alarmSetting: AlarmSetting?,
  override val otherMappings: List<OtherStringMapping>,
) : ToBeMappedYouTrackIssueInfo {
  init {
    require(durationFieldName != null || defaultDuration != null) {
      "Either durationFieldName or defaulrDuration must be non-null"
    }
  }
}
