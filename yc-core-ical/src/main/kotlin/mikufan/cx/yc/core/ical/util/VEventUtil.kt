package mikufan.cx.yc.core.ical.util

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun VEvent.setDtStartWithZoneId(zonedDateTime: ZonedDateTime) {
  setDtStartWithZoneId(zonedDateTime.toLocalDateTime(), zonedDateTime.zone)
//  add(DtStart(zonedDateTime))
}

fun VEvent.setDtEndWithZoneId(zonedDateTime: ZonedDateTime) {
  setDtEndWithZoneId(zonedDateTime.toLocalDateTime(), zonedDateTime.zone)
//  add(DtEnd(zonedDateTime))
}

fun VEvent.setDtStartWithZoneId(localDateTime: LocalDateTime, zoneId: ZoneId) {
  val dtStart = DtStart(localDateTime)
  dtStart.add<TzId>(net.fortuna.ical4j.model.parameter.TzId(zoneId.id))
  addProperty(dtStart)
}

fun VEvent.setDtEndWithZoneId(localDateTime: LocalDateTime, zoneId: ZoneId) {
  val dtEnd = DtEnd(localDateTime)
  dtEnd.add<TzId>(net.fortuna.ical4j.model.parameter.TzId(zoneId.id))
  addProperty(dtEnd)
}

fun VEvent.addCommonProperties(
  id: String?,
  summary: String?,
  description: String?,
) {
  id?.let { addProperty(Uid(it)) }
  summary?.let { addProperty(Summary(it)) }
  description?.let { addProperty(Description(it)) }
}

