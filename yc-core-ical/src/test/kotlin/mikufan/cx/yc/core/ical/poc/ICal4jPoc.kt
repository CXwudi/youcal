package mikufan.cx.yc.core.ical.poc

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.yc.core.ical.util.addCommonProperties
import mikufan.cx.yc.core.ical.util.setDtEndWithZoneId
import mikufan.cx.yc.core.ical.util.setDtStartWithZoneId
import mikufan.cx.yc.core.ical.util.toICalTimeZone
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Related
import net.fortuna.ical4j.model.property.*
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAmount

fun createVEvent(
  startZonedDateTime: ZonedDateTime,
  endZonedDateTime: ZonedDateTime,
  id: String? = null,
  summary: String? = null,
  description: String? = null,
): VEvent = VEvent().apply {
  setDtStartWithZoneId(startZonedDateTime)
  setDtEndWithZoneId(endZonedDateTime)
  addCommonProperties(id, summary, description)
}

fun createVEvent(
  startZonedDateTime: ZonedDateTime,
  duration: TemporalAmount,
  id: String? = null,
  summary: String? = null,
  description: String? = null,
): VEvent = VEvent().apply {
  setDtStartWithZoneId(startZonedDateTime)
  add(Duration(duration))
  addCommonProperties(id, summary, description)
}

/**
 * @date 2022-10-19
 * @author CX无敌
 */
class ICal4jPoc : ShouldSpec({
  context("VEVENT") {
    context("whole day event") {
      should("create a 3 day event") {
        val event = VEvent(
          LocalDate.of(2022, 5, 1),
          LocalDate.of(2022, 5, 4),
          "A 3 day event"
        ).apply {
          add(Description("Description of the event"))
        }
        event.getProperty<DtStart<*>>(Property.DTSTART).get().apply {
          date shouldBe LocalDate.of(2022, 5, 1)
        }
        event.getProperty<DtEnd<*>>(Property.DTEND).get().date shouldBe LocalDate.of(2022, 5, 4)
        println("event = \n$event")
        event.validate().entries.forEach {
          println("error = $it")
        }
      }

      should("create an one day event") {
        val event = VEvent(
          LocalDate.of(2022, 5, 1),
          "An one day event"
        ).apply {
          add(Description("Description of the event"))
        }
        event.getProperty<DtStart<*>>(Property.DTSTART).get().date shouldBe LocalDate.of(2022, 5, 1)
        event.getProperty<DtEnd<*>>(Property.DTEND).isEmpty shouldBe true
        println("event = \n$event")
      }
    }

    context("duration event") {
      should("create a 36 hours hackathon") {
        val event = createVEvent(
          ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          Duration.ofHours(36),
          summary = "A 36 hours hackathon"
        ).apply {
          add(Description("Description of the event"))
        }
        println("event = \n$event")
      }

      should("create a 36 hours hackathon with start end date time") {
        val event = createVEvent( // create a new event
          ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          ZonedDateTime.of(2022, 5, 2, 20, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          summary = "A 36 hours hackathon"
        ).apply {
          add(Description("Description of the event"))
        }
        println("event = \n$event")
      }

      xshould("create using timestamp and timezone explicitly") {
        // this won't work
        val vEvent = VEvent().apply {
          add(
            DtStart(Instant.ofEpochSecond(1671147000)).apply {
              add<TzId>(net.fortuna.ical4j.model.parameter.TzId("Asia/Shanghai"))
            }
          )
          add(Duration(Duration.ofHours(36)))
          add(Summary("A 36 hours hackathon"))
        }
        println("vEvent = \n$vEvent")
      }
    }
  }
  context("time") {
    println(ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")))
    println(ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneId.of("Canada/Eastern")))
    println(ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneId.of("Canada/Eastern")).toInstant().toEpochMilli() / 1000.0)
  }
  context("VALARM") {
    should("add an alarm") {
      val alarm = VAlarm().apply {
        val trigger = Trigger(Duration.ofMinutes(-10)).apply {
          add<RelatedTo>(Related.END)
        }
        add(Action("DISPLAY"))
        add(trigger)
      }
      val event = createVEvent( // create a new event
        ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
        ZonedDateTime.of(2022, 5, 2, 20, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
        summary = "A 36 hours hackathon"
      ).apply {
        add(Description("Description of the event"))
        add(alarm)
      }
      println("event = \n$event")
    }
  }

  context("VCALENDAR") {
    val javaZoneId = ZoneId.of("Canada/Eastern")

    should("create calendar with timezone and two events") {
      val alarm = VAlarm(Duration.ofMinutes(-10)).apply {
        add(Action("DISPLAY"))
        add(Description("A Reminder"))
      }
      val calendar = Calendar().apply {
        add(javaZoneId.toICalTimeZone().vTimeZone)
        add(ProdId("Calendar1"))
      }.fluentTarget.withDefaults().fluentTarget
      val event1 = createVEvent( // create a new event
        ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, javaZoneId.toICalTimeZone().toZoneId()),
        ZonedDateTime.of(2022, 5, 2, 20, 30, 0, 0, javaZoneId.toICalTimeZone().toZoneId()),
        "H1",
        "A 36 hours hackathon"
      ).apply {
        add(Description("Description of the event1"))
        add(alarm)
      }
      val event2 = VEvent(Instant.ofEpochMilli(1672628730081), Duration.ofHours(12), "A 12 hour event").apply {
        add(Uid("H2"))
        add(Description("Description of the event2"))
        add(alarm)
      }
      calendar.add(event1).add(event2)
      calendar.validate().entries.forEach {
        println("error = $it")
      }
      println("calendar = \n$calendar")
    }
  }
})
