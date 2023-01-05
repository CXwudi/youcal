package mikufan.cx.yc.cliapp.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.cliapp.config.IOConfig
import mikufan.cx.yc.core.ical.util.newCalendar
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import org.springframework.stereotype.Component

/**
 * @author CX无敌
 * 2023-01-04
 */
@Component
class CalendarCreator(
  ioConfig: IOConfig,
  private val dateTimeConfig: DateTimeConfig,
) {

  val outputFileName = ioConfig.outputFileName

  fun createCalendar(
    eventList: List<VEvent>,
  ): Calendar {
    log.info { "Assembling the calendar $outputFileName" }
    val zoneIdOrNull = if (dateTimeConfig.isZoneIdUsed) dateTimeConfig.zoneId else null
    return newCalendar(outputFileName, zoneIdOrNull, eventList)
  }
}

private val log = KInlineLogging.logger()
