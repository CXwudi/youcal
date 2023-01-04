package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.model.StringMappableVEventField
import mikufan.cx.yc.core.ical.model.StringMapping
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.loadResourceAsString
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Transp

class OtherFieldsSetterTest : ShouldSpec({
  val setter = OtherFieldsSetter()
  context("other mapping") {
    val issues =
      ObjectMapper().readTree(loadResourceAsString("sample-json/with-all-custom-fields-rsp-2023-but-missing-values.json"))
    val mappings = OtherStringMappings(
      listOf(
        StringMapping("State", "Unresolved", StringMappableVEventField.STATUS),
        StringMapping("Assignee", "Unassigned", StringMappableVEventField.ATTENDEE),
        StringMapping("", Transp.VALUE_OPAQUE, StringMappableVEventField.TRANSP),
        StringMapping("Submodule", "No module", StringMappableVEventField.CATEGORIES),
      )
    )
    issues.forEach { json ->
      json as YouTrackIssueJson
      should("works on ${json["idReadable"].asText()}") {
        val vEvent = VEvent()
        setter.doMapAndSet(vEvent, json, mappings)
        println(vEvent)
        vEvent.apply {
          propertyList.all.size shouldBe 5
        }
      }
    }
  }
})
