package mikufan.cx.yc.core.ical.model.deprecated

import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author CX无敌
 * 2023-01-01
 */
@Deprecated("seems unneeded")
sealed interface EventDateTime

class OneDayEventDate(
  private val startInternal: ZonedDateTime,
) : EventDateTime {
  val start: LocalDate = startInternal.toLocalDate()

  fun startOf(zoneId: ZoneId): LocalDate {
    return startInternal.withZoneSameInstant(zoneId).toLocalDate()
  }
}

data class StartEndEventDateTimes(
  val start: ZonedDateTime,
  val end: ZonedDateTime,
) : EventDateTime

data class DurationEventDateTimes(
  val start: ZonedDateTime,
  val duration: Duration,
) : EventDateTime
