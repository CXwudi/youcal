package mikufan.cx.yci.icalcore.util

import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import java.time.ZoneId

internal val timeZoneReg = TimeZoneRegistryFactory.getInstance().createRegistry()

/**
 * get the iCal4j time zone object from a java time zone id
 *
 * the returned iCal4j time zone may not be the same as the passed in java time zone id.
 * e.g. Canada/Eastern is mapped to America/Toronto
 * @receiver ZoneId
 * @return TimeZone
 */
fun ZoneId.toICalTimeZone(): TimeZone = timeZoneReg.getTimeZone(id)
fun TimeZone.toZoneId(): ZoneId = ZoneId.of(id)
