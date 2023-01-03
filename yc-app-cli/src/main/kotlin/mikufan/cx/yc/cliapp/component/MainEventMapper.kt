package mikufan.cx.yc.cliapp.component

import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.inlinelogging.KInlineLogging
import net.fortuna.ical4j.model.component.VEvent
import org.springframework.stereotype.Service

/**
 * @author CX无敌
 * 2023-01-02
 */
@Service
class MainEventMapper() : (ObjectNode) -> VEvent {

  override fun invoke(task: ObjectNode): VEvent {
    log.info { "Start mapping $task" }
    TODO()
  }
}

private val log = KInlineLogging.logger()
