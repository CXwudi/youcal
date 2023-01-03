package mikufan.cx.yc.core.ical.model

import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-03
 */
sealed interface DateTimeFieldInfo

data class OneDayIssueDateTimeFieldInfo(
  val fieldName: String,
) : DateTimeFieldInfo

data class DurationDateTimeIssueDateTimeFieldInfo(
  val startFieldName: String,
  val durationFieldName: String?,
  val defaultDuration: Duration,
) : DateTimeFieldInfo
