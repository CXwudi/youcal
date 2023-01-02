package mikufan.cx.yc.cliapp.component.debug

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * @author CX无敌
 * 2023-01-01
 */
@Component
class AllBeansLister : ApplicationListener<ApplicationStartedEvent> {

  /**
   * Handle an application event.
   * @param event the event to respond to
   */
  override fun onApplicationEvent(event: ApplicationStartedEvent) {
    log.debug { "All beans in the context: ${event.applicationContext.beanDefinitionNames.joinToString()}" }
  }
}

private val log = KInlineLogging.logger()
