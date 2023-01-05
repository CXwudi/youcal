package mikufan.cx.yc.cliapp.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.core.ical.component.EventMapper
import mikufan.cx.yc.core.ical.model.exception.MappingException
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.debugName
import net.fortuna.ical4j.model.component.VEvent
import org.springframework.stereotype.Service

/**
 * @author CX无敌
 * 2023-01-02
 */
@Service
class MainEventMapper(
  private val eventMapper: EventMapper,
  private val infoCreator: ToBeMappedYouTrackIssueInfoCreator,
) : (YouTrackIssueJson) -> Result<VEvent> {

  override fun invoke(task: YouTrackIssueJson): Result<VEvent> {
    return try {
      val toBeMappedYouTrackIssueInfo = infoCreator.createInfo(task)
      Result.success(eventMapper.doMap(toBeMappedYouTrackIssueInfo))
    } catch (e: MappingException) {
      log.warn(e) { "Failed to map the YouTrack ${task.debugName} to VEvent, skipping it" }
      Result.failure(e)
    }
  }
}

private val log = KInlineLogging.logger()
