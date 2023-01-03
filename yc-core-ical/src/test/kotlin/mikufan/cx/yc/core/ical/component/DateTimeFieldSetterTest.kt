package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.failure
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.core.ical.model.OneDayIssueDateTimeFieldInfo
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.loadResourceAsString
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import java.time.ZoneId

class DateTimeFieldSetterTest : ShouldSpec({

  val dateTimeFieldSetter = DateTimeFieldSetter()
  val issues = ObjectMapper().readTree(loadResourceAsString("sample-json/with-prio-state-assignee-due-rsp.json"))
  context("setting one date event") {
    issues.forEach { json ->
      val issue = json as YouTrackIssueJson
      should("works on ${issue["idReadable"].asText()}") {
        try {
          val vEvent = VEvent()
          dateTimeFieldSetter.doMapAndSet(
            vEvent,
            issue,
            OneDayIssueDateTimeFieldInfo("Due Date", ZoneId.of("Canada/Eastern"))
          )
          println(vEvent)
          vEvent.propertyList.all.any { it is DtStart<*> }
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
