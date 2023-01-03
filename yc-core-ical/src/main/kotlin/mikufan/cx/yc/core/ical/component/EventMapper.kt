package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.yc.core.ical.model.OneDayIssueInfo
import mikufan.cx.yc.core.ical.model.ToBeMappedYouTrackIssueInfo
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackDefaultDateTime
import mikufan.cx.yc.core.ical.util.YouTrackType
import net.fortuna.ical4j.model.component.VEvent
import java.time.Instant

/**
 * @author CX无敌
 * 2023-01-02
 */
class EventMapper {

  fun doMap(toBeMapped: ToBeMappedYouTrackIssueInfo): VEvent {
    val mapped = when (toBeMapped) {
      is OneDayIssueInfo -> mapOneDayIssue(toBeMapped)
      else -> TODO("not implemented yet")
    }
    return mapped
  }

  private fun mapOneDayIssue(toBeMapped: OneDayIssueInfo): VEvent {
    val (json, dateTimeFieldInfo, alarmSetting, otherMappings) = toBeMapped
    val fieldName = dateTimeFieldInfo.fieldName
    val startDateStr = if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(fieldName)) {
      json[fieldName].asText()
    } else {
      val customField = json.getCustomField(fieldName, YouTrackType.DATE_ISSUE_CUSTOM_FIELD)
      customField["value"].asText()
    }
    val startDate = Instant.ofEpochMilli(startDateStr.toLong())
    TODO()
  }

  private fun ObjectNode.getCustomField(fieldName: String, expectedType: String = ""): ObjectNode {
    val customField: ObjectNode = get("customFields").find { it["name"].asText() == fieldName } as ObjectNode?
      ?: throw MappingException("Cannot find custom field $fieldName in $this")
    if (customField["\$type"].asText() != expectedType) {
      throw MappingException("Custom field $fieldName is not of type $expectedType")
    }
    return customField
  }
}
