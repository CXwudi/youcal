package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.JsonNode
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.core.ical.model.DateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.DurationDateTimeIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.OneDayIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.*
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import java.time.Instant

/**
 * Map the date time field value into the [VEvent]'s start end date time field.
 *
 * Based on the type of [DateTimeFieldInfo], it will map to different [VEvent]'s fields.
 *
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
    log.info { "Mapping to one day VEvent for ${issueJson.debugName}" }
    val fieldName = dateTimeFieldInfo.fieldName
    val startDateJsonNode = extractDateFieldValueJsonNode(fieldName, issueJson)
    if (startDateJsonNode.isNull) {
      log.warn { "Can not map ${issueJson.debugName} to VEvent due to missing proper start date" }
      throw MappingException("Start date field $fieldName is null or blank, skipping the mapping of this issue")
    } else {
      val startDateInstant = Instant.ofEpochMilli(startDateJsonNode.asLong())
      val startDate = startDateInstant.atZone(dateTimeFieldInfo.zoneId).toLocalDate()
      vEvent.addProperty(DtStart(startDate))
    }
  }

  private fun extractDateFieldValueJsonNode(fieldName: String, issueJson: YouTrackIssueJson): JsonNode {
    val startDateJsonNode = if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(fieldName)) {
      issueJson[fieldName]
    } else {
      val customField = issueJson.getCustomFieldOrThrow(fieldName, YouTrackType.DATE_ISSUE_CUSTOM_FIELD) {
        log.warn { "Can not map ${issueJson.debugName} to VEvent due to missing the start date field" }
        MappingException("Start date field $fieldName is missing, skipping the mapping of ${issueJson.debugName}")
      }
      customField["value"]
    }
    return startDateJsonNode
  }
}

private val log = KInlineLogging.logger()
