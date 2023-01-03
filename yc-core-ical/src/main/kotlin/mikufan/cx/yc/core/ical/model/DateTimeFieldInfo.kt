package mikufan.cx.yc.core.ical.model

import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

/**
 * @author CX无敌
 * 2023-01-03
 */
sealed interface DateTimeFieldInfo

data class OneDayIssueDateTimeFieldInfo(
  val fieldName: String,
  /**
   * used by converting the YouTrack Instant time to [LocalDate]
   */
  val zoneId: ZoneId,
) : DateTimeFieldInfo

data class DurationDateTimeIssueDateTimeFieldInfo(
  val startFieldName: String,
  val durationFieldName: String?,
  val defaultDuration: Duration,
) : DateTimeFieldInfo
