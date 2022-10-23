package mikufan.cx.yci.icalcore.util

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAmount

fun VEvent.setDtStartWithZoneId(zonedDateTime: ZonedDateTime) {
  setDtStartWithZoneId(zonedDateTime.toLocalDateTime(), zonedDateTime.zone)
}

fun VEvent.setDtEndWithZoneId(zonedDateTime: ZonedDateTime) {
  setDtEndWithZoneId(zonedDateTime.toLocalDateTime(), zonedDateTime.zone)
}

fun VEvent.setDtStartWithZoneId(localDateTime: LocalDateTime, zoneId: ZoneId) {
  val dtStart = DtStart(localDateTime)
  dtStart.add<TzId>(net.fortuna.ical4j.model.parameter.TzId(zoneId.id))
  add(dtStart)
}

fun VEvent.setDtEndWithZoneId(localDateTime: LocalDateTime, zoneId: ZoneId) {
  val dtEnd = DtEnd(localDateTime)
  dtEnd.add<TzId>(net.fortuna.ical4j.model.parameter.TzId(zoneId.id))
  add(dtEnd)
}

fun VEvent(
  startZonedDateTime: ZonedDateTime,
  endZonedDateTime: ZonedDateTime,
  id: String? = null,
  summary: String? = null,
  description: String? = null,
): VEvent = VEvent().apply {
  setDtStartWithZoneId(startZonedDateTime)
  setDtEndWithZoneId(endZonedDateTime)
  addMoreProperties(id, summary, description)
}

fun VEvent(
  startZonedDateTime: ZonedDateTime,
  duration: TemporalAmount,
  id: String? = null,
  summary: String? = null,
  description: String? = null,
): VEvent = VEvent().apply {
  setDtStartWithZoneId(startZonedDateTime)
  add(Duration(duration))
  addMoreProperties(id, summary, description)
}

private fun VEvent.addMoreProperties(
  id: String?,
  summary: String?,
  description: String?,
) {
  id?.let { add(Uid(it)) }
  summary?.let { add(Summary(it)) }
  description?.let { add(Description(it)) }
}
