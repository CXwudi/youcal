package mikufan.cx.yc.core.ical.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.core.ical.model.ToBeMappedYouTrackIssueInfo
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.addCommonProperties
import mikufan.cx.yc.core.ical.util.debugName
import net.fortuna.ical4j.model.component.VEvent

/**
 * @author CX无敌
 * 2023-01-02
 */
class EventMapper(
  private val dateTimeFieldSetter: DateTimeFieldSetter,
  private val alarmMapper: AlarmMapper,
  private val otherFieldsSetter: OtherFieldsSetter,
) {

  fun doMap(toBeMapped: ToBeMappedYouTrackIssueInfo): VEvent {
    val json = toBeMapped.json
    val dateTimeFieldInfo = toBeMapped.dateTimeFieldInfo
    log.info { "Start mapping ${json.debugName} to VEvent" }
    val vEvent = VEvent()
    dateTimeFieldSetter.doMapAndSet(vEvent, json, dateTimeFieldInfo)
    vEvent.addCommonPropertiesFrom(json)
    val alarmSetting = toBeMapped.alarmSetting
    alarmMapper.createAlarm(json, alarmSetting)?.let {
      vEvent.add(it)
    }
    val otherMappings = toBeMapped.otherMappings
    otherFieldsSetter.doMapAndSet(vEvent, json, otherMappings)
    log.info { "Done mapping ${json.debugName} to VEvent" }
    return vEvent
  }

  private fun VEvent.addCommonPropertiesFrom(json: YouTrackIssueJson) {
    val idReadable = json["idReadable"].asText()
    val summary = "[$idReadable] ${json["summary"].asText()}"
    val descriptionNode = json["description"]
    val description = if (descriptionNode.isNull) null else descriptionNode.asText()
    addCommonProperties(idReadable, summary, description)
  }
}

private val log = KInlineLogging.logger()
