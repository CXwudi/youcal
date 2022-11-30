package mikufan.cx.yc.icalcore.component

import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import org.springframework.stereotype.Component

/**
 * @date 2022-10-10
 * @author CX无敌
 */
@Component
class IssueToVEventConverter {

  private val registry = TimeZoneRegistryFactory.getInstance().createRegistry()

  // FIXME: need to define the inputs first, and rework this method
//  fun convert(issueJson: ObjectNode, dateFieldName: DateFieldName, timeZoneString: String): VEvent {
//    val zoneId = ZoneId.of(timeZoneString)
//    val summary = issueJson["summary"].asText()
//    val vEvent = when (dateFieldName) {
//      is WholeDayEventDateFieldName -> {
//        val dateLong = issueJson[dateFieldName.fieldName].asLong()
//        val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateLong), zoneId)
//        VEvent(dateTime, summary)
//      }
//
//      is TimeEventDateFieldName -> {
//        val startDateLong = issueJson[dateFieldName.startDateFieldName].asLong()
//        val endDateLong = issueJson[dateFieldName.endDateFieldName].asLong()
//        val startDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateLong), zoneId)
//        val endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateLong), zoneId)
//        VEvent(startDate, endDate, summary)
//      }
//    }
//    vEvent.propertyList.apply {
//      add(Uid(issueJson["idReadable"].asText()))
//      add(registry.getTimeZone(zoneId.id).vTimeZone.getProperty<TzId>(Property.TZID).orElseThrow())
//    }
//    return VEvent()
//  }
}
