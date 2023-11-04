package mikufan.cx.yc.core.ical.util

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import java.time.ZoneId

/**
 * @author CX无敌
 * 2023-01-04
 */
fun newCalendar(
  prodId: String,
  zoneId: ZoneId?,
  vararg events: VEvent,
) = Calendar().apply { addCommonStuffs(prodId, zoneId, *events) }

fun newCalendar(
  prodId: String,
  zoneId: ZoneId?,
  events: Collection<VEvent>,
) = Calendar().apply { addCommonStuffs(prodId, zoneId, *events.toTypedArray()) }

fun Calendar.addCommonStuffs(
  prodId: String,
  zoneId: ZoneId?,
  vararg events: VEvent,
) {
  addProperty(ProdId(prodId))
  addProperty(Version(Version.VALUE_2_0, Version.VALUE_2_0))
  zoneId?.let { addComponent(it.toICalTimeZone().vTimeZone) }
  events.forEach { addComponent(it) }
}
