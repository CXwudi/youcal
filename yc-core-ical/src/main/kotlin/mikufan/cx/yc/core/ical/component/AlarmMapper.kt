package mikufan.cx.yc.core.ical.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.core.ical.model.AlarmSetting
import mikufan.cx.yc.core.ical.model.ShiftBasedOn
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.YouTrackType
import mikufan.cx.yc.core.ical.util.debugName
import mikufan.cx.yc.core.ical.util.getCustomField
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.parameter.Related
import net.fortuna.ical4j.model.property.Action
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.RelatedTo
import net.fortuna.ical4j.model.property.Trigger
import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-03
 */
class AlarmMapper {
  fun createAlarm(json: YouTrackIssueJson, alarmSetting: AlarmSetting?): VAlarm? =
    alarmSetting?.let { (durationFieldName, isNegativeDurationField, defaultDuration, shiftBasedOn) ->
      log.info { "Creating VAlarm based on alarm setting for ${json.debugName}" }

      val (duration, descStr) = getDurationAndDescription(
        durationFieldName,
        json,
        isNegativeDurationField,
        defaultDuration
      )
      val fullDescription = when (shiftBasedOn) {
        ShiftBasedOn.START -> "$descStr start date"
        ShiftBasedOn.END -> "$descStr end date"
      }
      val trigger = Trigger(duration).apply {
        if (shiftBasedOn == ShiftBasedOn.END) {
          add<RelatedTo>(Related.END)
        }
      }

      VAlarm().apply {
        add(Action(Action.VALUE_DISPLAY))
        add(trigger)
        add(Description(fullDescription))
      }
    }

  private fun getDurationAndDescription(
    durationFieldName: String?,
    json: YouTrackIssueJson,
    isNegativeDurationField: Boolean,
    defaultDuration: Duration,
  ): Pair<Duration, String> {
    val descriptionFirstPart = "A Reminder for ${json["idReadable"].asText()}"
    return if (!durationFieldName.isNullOrBlank()) {
      val durationFromField = tryGetDurationFrom(json, durationFieldName, isNegativeDurationField)
      if (durationFromField != null) {
        log.debug { "Using duration from field $durationFieldName for ${json.debugName}" }
        val descrptionLastPart =
          "with duration $durationFromField " + if (isNegativeDurationField) "before" else "after"
        durationFromField to "$descriptionFirstPart $descrptionLastPart"
      } else {
        defaultDuration to "$descriptionFirstPart that fallbacks to default duration $defaultDuration related to"
      }
    } else {
      log.debug { "no duration field set for ${json.debugName}, using default duration directly" }
      defaultDuration to "$descriptionFirstPart with default duration $defaultDuration related to"
    }
  }

  private fun tryGetDurationFrom(json: YouTrackIssueJson, fieldName: String, isNegative: Boolean): Duration? {
    val field = json.getCustomField(fieldName, YouTrackType.PERIOD_ISSUE_CUSTOM_FIELD)
    val valueJson = field["value"]
    if (valueJson.isNull) {
      log.warn { "Field $fieldName contains null alarm, fallback to use default duration" }
      return null
    }
    val durationString = valueJson["id"].asText()
    return Duration.parse(durationString).let {
      if (isNegative) {
        it.negated()
      } else {
        it
      }
    }
  }
}

private val log = KInlineLogging.logger()
