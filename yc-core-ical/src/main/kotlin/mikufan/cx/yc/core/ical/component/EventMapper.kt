package mikufan.cx.yc.core.ical.component

import mikufan.cx.yc.core.ical.model.ToBeMappedYouTrackIssueInfo
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.addCommonProperties
import net.fortuna.ical4j.model.component.VEvent

/**
 * @author CX无敌
 * 2023-01-02
 */
class EventMapper(
  private val dateTimeFieldSetter: DateTimeFieldSetter,
) {

  fun doMap(toBeMapped: ToBeMappedYouTrackIssueInfo): VEvent {
    val json = toBeMapped.json
    val dateTimeFieldInfo = toBeMapped.dateTimeFieldInfo
    val vEvent = VEvent()
    dateTimeFieldSetter.doMapAndSet(vEvent, json, dateTimeFieldInfo)
    vEvent.addCommonPropertiesFrom(json)

    return vEvent
  }

  private fun VEvent.addCommonPropertiesFrom(json: YouTrackIssueJson) {
    val idReadable = json["idReadable"].asText()
    val summary = "$idReadable - ${json["summary"].asText()}"
    val description = json["description"].asText()
    addCommonProperties(idReadable, summary, description)
  }
}
