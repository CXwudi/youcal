package mikufan.cx.yc.cliapp.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.cliapp.config.IOConfig
import net.fortuna.ical4j.model.Calendar
import org.springframework.stereotype.Component
import kotlin.io.path.bufferedWriter

/**
 * @author CX无敌
 * 2023-01-04
 */
@Component
class CalendarWriter(
  ioConfig: IOConfig,
) {

  private val outputFile = ioConfig.outputFile

  fun writeCalendar(calendar: Calendar) {
    log.info { "Writing calendar to $outputFile" }
    outputFile.bufferedWriter().use { writer ->
      writer.write(calendar.toString())
    }
  }
}

private val log = KInlineLogging.logger()
