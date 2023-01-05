package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.failure
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.core.ical.model.*
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.loadResourceAsString
import net.fortuna.ical4j.model.property.Transp
import java.time.Duration
import java.time.ZoneId

class EventMapperIT : ShouldSpec({

  val eventMapper = EventMapper(DateTimeFieldSetter(), AlarmMapper(), OtherFieldsSetter())
  listOf(
    "sample-json/with-prio-state-assignee-due-rsp.json",
    "sample-json/with-all-custom-fields-rsp-2023-but-missing-values.json"
  ).forEach { resourceName ->
    context("mapping of $resourceName to whole day events without alarm") {
      val issues = ObjectMapper().readTree(loadResourceAsString(resourceName))
      val dateFieldInfo = OneDayIssueDateTimeFieldInfo("Due Date", ZoneId.of("Canada/Eastern"))
      issues.forEach { json ->
        should("works on ${json["idReadable"].asText()}") {
          val toBeMapped =
            OneDayIssueInfo(json as YouTrackIssueJson, dateFieldInfo, null, OtherStringMappings(emptyList()))
          try {
            val vEvent = eventMapper.doMap(toBeMapped)
            println(vEvent)
          } catch (e: MappingException) {
            e.printStackTrace()
          } catch (e: Exception) {
            e.printStackTrace()
            failure("unexpected exception", e)
          }
        }
      }
    }
    context("mapping of $resourceName to whole day events with alarm") {
      val issues = ObjectMapper().readTree(loadResourceAsString(resourceName))
      val dateFieldInfo = OneDayIssueDateTimeFieldInfo("Due Date", ZoneId.of("Canada/Eastern"))
      issues.forEach { json ->
        should("works on ${json["idReadable"].asText()}") {
          val alarmSetting = AlarmSetting("Estimation", true, Duration.ofMinutes(-15))
          val toBeMapped =
            OneDayIssueInfo(json as YouTrackIssueJson, dateFieldInfo, alarmSetting, OtherStringMappings(emptyList()))
          try {
            val vEvent = eventMapper.doMap(toBeMapped)
            println(vEvent)
          } catch (e: MappingException) {
            e.printStackTrace()
          } catch (e: Exception) {
            e.printStackTrace()
            failure("unexpected exception", e)
          }
        }
      }
    }

    context("mapping of $resourceName to whole day events with alarm and other fields") {
      val issues = ObjectMapper().readTree(loadResourceAsString(resourceName))
      val dateFieldInfo = OneDayIssueDateTimeFieldInfo("Due Date", ZoneId.of("Canada/Eastern"))
      issues.forEach { json ->
        should("works on ${json["idReadable"].asText()}") {
          val alarmSetting = AlarmSetting("Estimation", true, Duration.ofMinutes(-15))
          val otherMappings = OtherStringMappings(
            listOf(
              StringMapping("State", "Unresolved", StringMappableVEventField.STATUS),
              StringMapping("Assignee", "Unassigned", StringMappableVEventField.ATTENDEE),
              StringMapping("", Transp.VALUE_OPAQUE, StringMappableVEventField.TRANSP),
              StringMapping("Submodule", "No module", StringMappableVEventField.CATEGORIES),
            )
          )
          val toBeMapped =
            OneDayIssueInfo(json as YouTrackIssueJson, dateFieldInfo, alarmSetting, otherMappings)
          try {
            val vEvent = eventMapper.doMap(toBeMapped)
            println(vEvent)
          } catch (e: MappingException) {
            e.printStackTrace()
          } catch (e: Exception) {
            e.printStackTrace()
            failure("unexpected exception", e)
          }
        }
      }
    }
  }
})
