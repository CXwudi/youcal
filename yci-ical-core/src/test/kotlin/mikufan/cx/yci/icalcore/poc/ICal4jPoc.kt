package mikufan.cx.yci.icalcore.poc

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yci.icalcore.util.VEvent
import mikufan.cx.yci.icalcore.util.toICalTimeZone
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.ProdId
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @date 2022-10-19
 * @author CX无敌
 */
@SpringBootTest
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
        val event = VEvent(
          ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          Duration.ofHours(36),
          summary = "A 36 hours hackathon"
        ).apply {
          add(Description("Description of the event"))
        }
        println("event = \n$event")
      }

      should("create a 36 hours hackathon with start end date time") {
        val event = VEvent( // create a new event
          ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          ZonedDateTime.of(2022, 5, 2, 20, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
          summary = "A 36 hours hackathon"
        ).apply {
          add(Description("Description of the event"))
        }
        println("event = \n$event")
      }
    }
  }
  context("time") {
    println(ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneId.of("Asia/Shanghai")))
    println(ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneId.of("Canada/Eastern")))
  }
  context("VCALENDAR") {
    val javaZoneId = ZoneId.of("Canada/Eastern")

    should("create calendar with timezone and one event") {
      val calendar = Calendar().apply {
        add(javaZoneId.toICalTimeZone().vTimeZone)
        add(ProdId("Calendar1"))
      }.fluentTarget.withDefaults().fluentTarget
      val event = VEvent( // create a new event
        ZonedDateTime.of(2022, 5, 1, 8, 30, 0, 0, javaZoneId.toICalTimeZone().toZoneId()),
        ZonedDateTime.of(2022, 5, 2, 20, 30, 0, 0, javaZoneId.toICalTimeZone().toZoneId()),
        "H1",
        "A 36 hours hackathon"
      ).apply {
        add(Description("Description of the event"))
      }
      calendar.add(event)
      calendar.validate().entries.forEach {
        println("error = $it")
      }
      println("calendar = \n$calendar")
    }
  }
})

private val log = KInlineLogging.logger()
