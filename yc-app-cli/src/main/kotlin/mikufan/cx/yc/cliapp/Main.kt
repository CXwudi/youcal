package mikufan.cx.yc.cliapp

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.cliapp.component.IssuesGetter
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * @author CX无敌
 * 2023-01-01
 */

fun main(args: Array<String>) {
  runApplication<Main>(*args).use { it.getBean<Main>().run() }
}

private val log = KInlineLogging.logger()

@SpringBootApplication
@ConfigurationPropertiesScan
class Main(
  private val issuesGetter: IssuesGetter,
) : Runnable {
  override fun run() {
    log.info { "Getting issues" }
    val issuesIterator = issuesGetter.issuesIterator()
    for (issue in issuesIterator) {
      log.info { "Got issue: $issue" }
    }
  }
}
