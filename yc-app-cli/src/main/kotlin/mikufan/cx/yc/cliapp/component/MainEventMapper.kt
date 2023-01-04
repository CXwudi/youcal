package mikufan.cx.yc.cliapp.component

import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.cliapp.config.AlarmConfig
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.model.exception.MappingException
import net.fortuna.ical4j.model.component.VEvent
import org.springframework.stereotype.Service

/**
 * @author CX无敌
 * 2023-01-02
 */
@Service
class MainEventMapper(
  private val dateTimeConfig: DateTimeConfig,
  private val alarmConfig: AlarmConfig,
  private val otherStringMappings: OtherStringMappings,
) : (ObjectNode) -> Result<VEvent> {

  override fun invoke(task: ObjectNode): Result<VEvent> {
    log.info { "Start mapping $task" }
    try {
      TODO()
    } catch (e: MappingException) {
      return Result.failure(e)
    }
  }
}

private val log = KInlineLogging.logger()
