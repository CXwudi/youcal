package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mikufan.cx.yc.core.ical.model.AlarmSetting
import mikufan.cx.yc.core.ical.util.loadResourceAsString
import net.fortuna.ical4j.model.property.Action
import net.fortuna.ical4j.model.property.Trigger
import java.time.Duration

class AlarmMapperTest : ShouldSpec({

  val alarmMapper = AlarmMapper()
  context("mapping alarm") {
    val issues = ObjectMapper().readTree(loadResourceAsString("sample-json/with-all-custom-fields-rsp-2023.json"))
    context("on null alarm setting") {
      issues.forEach { json ->
        json as ObjectNode
        should("works on ${json["idReadable"].asText()}") {
          alarmMapper.createAlarm(json, null) shouldBe null
        }
      }
    }

    context("on only default duration") {
      issues.forEach { json ->
        json as ObjectNode
        should("works on ${json["idReadable"].asText()}") {
          val alarm = alarmMapper.createAlarm(json, AlarmSetting("", true, Duration.ofMinutes(-30)))
          println(alarm)
          alarm shouldNotBe null
          alarm!!.propertyList.all.apply {
            size shouldBe 3
            any { it is Action && it.value == Action.VALUE_DISPLAY } shouldBe true
            any { it is Trigger && it.value == "-PT30M" } shouldBe true
          }
        }
      }
    }

    context("on specified duration field with default duration") {
      issues.forEach { json ->
        json as ObjectNode
        should("works on ${json["idReadable"].asText()}") {
          val alarm = alarmMapper.createAlarm(json, AlarmSetting("Estimation", true, Duration.ofMinutes(-30)))
          println(alarm)
          alarm shouldNotBe null
          alarm!!.propertyList.all.apply {
            size shouldBe 3
          }
        }
      }
    }

    context("on specified duration field only") {
      issues.forEach { json ->
        json as ObjectNode
        should("works on ${json["idReadable"].asText()}") {
          val alarm = alarmMapper.createAlarm(json, AlarmSetting("Estimation", true, null))
          println(alarm)
          if (json["idReadable"].asText() == "AL-10") {
            alarm shouldBe null
          } else {
            alarm shouldNotBe null
            alarm!!.propertyList.all.apply {
              size shouldBe 3
            }
          }
        }
      }
    }
  }
})
