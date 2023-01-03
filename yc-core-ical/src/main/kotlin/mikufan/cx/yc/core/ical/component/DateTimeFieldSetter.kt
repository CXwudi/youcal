package mikufan.cx.yc.core.ical.component

import mikufan.cx.yc.core.ical.model.DateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.DurationDateTimeIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.OneDayIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackDefaultDateTime
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.YouTrackType
import mikufan.cx.yc.core.ical.util.getCustomField
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import java.time.Instant

/**
 * @author CX无敌
 * 2023-01-03
 */
class DateTimeFieldSetter {
  fun doMapAndSet(vEvent: VEvent, issueJson: YouTrackIssueJson, dateTimeFieldInfo: DateTimeFieldInfo) {
    when (dateTimeFieldInfo) {
      is OneDayIssueDateTimeFieldInfo -> mapAndSetOneDayIssue(vEvent, issueJson, dateTimeFieldInfo)
      is DurationDateTimeIssueDateTimeFieldInfo -> TODO("not implemented")
    }
  }

  private fun mapAndSetOneDayIssue(
    vEvent: VEvent,
    issueJson: YouTrackIssueJson,
    dateTimeFieldInfo: OneDayIssueDateTimeFieldInfo,
  ) {
    val fieldName = dateTimeFieldInfo.fieldName
    val startDateStr = extractDateFieldValue(fieldName, issueJson)
    val startDate = Instant.ofEpochMilli(startDateStr.toLong())
    // TODO need to convert to local Date
    vEvent.add(DtStart(startDate))
  }

  private fun extractDateFieldValue(fieldName: String, issueJson: YouTrackIssueJson): String {
    val startDateStr = if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(fieldName)) {
      issueJson[fieldName].asText()
    } else {
      val customField = issueJson.getCustomField(fieldName, YouTrackType.DATE_ISSUE_CUSTOM_FIELD)
      customField["value"].asText()
    }
    if (startDateStr.isNullOrBlank() || startDateStr == "null") {
      throw MappingException("Date field $fieldName is null or blank, skipping the mapping of this issue")
    } else {
      return startDateStr
    }
  }
}
