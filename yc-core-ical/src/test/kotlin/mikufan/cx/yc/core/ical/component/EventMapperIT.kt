package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.failure
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.core.ical.model.AlarmSetting
import mikufan.cx.yc.core.ical.model.OneDayIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.OneDayIssueInfo
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.loadResourceAsString
import java.time.Duration
import java.time.ZoneId

class EventMapperIT : ShouldSpec({

  val eventMapper = EventMapper(DateTimeFieldSetter(), AlarmMapper(), OtherFieldsSetter())
  context("mapping of the YouTrack Issue Json to a whole day event without alarm") {
    val issues = ObjectMapper().readTree(loadResourceAsString("sample-json/with-prio-state-assignee-due-rsp.json"))
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
  context("mapping of YouTrack Issue Json to a whole day event with alarm") {
    val issues = ObjectMapper().readTree(loadResourceAsString("sample-json/with-all-custom-fields-rsp-2023.json"))
    val dateFieldInfo = OneDayIssueDateTimeFieldInfo("Due Date", ZoneId.of("Canada/Eastern"))
    issues.forEach { json ->
      should("works on ${json["idReadable"].asText()}") {
        val alarmSetting = AlarmSetting("Estimation", true, Duration.ofHours(-3))
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
})