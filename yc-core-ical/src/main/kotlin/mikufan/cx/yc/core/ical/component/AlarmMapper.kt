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
  fun createAlarm(json: YouTrackIssueJson, alarmSetting: AlarmSetting?): VAlarm? {
    if (alarmSetting == null) {
      log.info { "No alarm setting for ${json.debugName}, skipping alarm creation" }
      return null
    }

    val (durationFieldName, isNegativeDurationField, defaultDuration, shiftBasedOn) = alarmSetting
    log.info { "Try creating VAlarm based on alarm setting for ${json.debugName}" }
    val (duration, descStr) = tryGetDurationAndDescription(
      durationFieldName,
      json,
      isNegativeDurationField,
      defaultDuration
    ) ?: return null
    val fullDescription = when (shiftBasedOn) {
      ShiftBasedOn.START -> "$descStr start date"
      ShiftBasedOn.END -> "$descStr end date"
    }
    val trigger = Trigger(duration).apply {
      if (shiftBasedOn == ShiftBasedOn.END) {
        add<RelatedTo>(Related.END)
      }
    }

    return VAlarm().apply {
      add(Action(Action.VALUE_DISPLAY))
      add(trigger)
      add(Description(fullDescription))
    }
  }

  private fun tryGetDurationAndDescription(
    durationFieldName: String,
    json: YouTrackIssueJson,
    isNegativeDurationField: Boolean,
    defaultDuration: Duration?,
  ): Pair<Duration, String>? {
    val descriptionFirstPart = "A Reminder for ${json["idReadable"].asText()}"
    return if (durationFieldName.isNotBlank()) {
      val durationFromField = tryGetDurationFrom(json, durationFieldName, isNegativeDurationField)
      if (durationFromField != null) {
        log.info { "Creating VAlarm using duration from field $durationFieldName for ${json.debugName}" }
        val descrptionLastPart =
          "with duration $durationFromField " + if (isNegativeDurationField) "before" else "after"
        durationFromField to "$descriptionFirstPart $descrptionLastPart"
      } else if (defaultDuration != null) {
        log.info { "Creating VAlarm using fallback default duration" }
        defaultDuration to "$descriptionFirstPart that fallbacks to default duration $defaultDuration related to"
      } else {
        log.info {
          "No default duration set while missing duration value " +
              "from field $durationFieldName for ${json.debugName}, skip setting the alarm"
        }
        return null
      }
    } else if (defaultDuration != null) {
      log.info { "Creating VAlarm using default duration" }
      defaultDuration to "$descriptionFirstPart that fallbacks to default duration $defaultDuration related to"
    } else {
      log.warn { "This shouldn't happen, but we traded it as no alarm" }
      return null
    }
  }

  private fun tryGetDurationFrom(json: YouTrackIssueJson, fieldName: String, isNegative: Boolean): Duration? {
    val field = json.getCustomField(fieldName, YouTrackType.PERIOD_ISSUE_CUSTOM_FIELD)
    val valueJson = field["value"]
    if (valueJson.isNull) {
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
