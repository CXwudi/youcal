package mikufan.cx.yc.cliapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.cliapp.component.IssuesGetter
import mikufan.cx.yc.cliapp.component.MainEventMapper
import net.fortuna.ical4j.model.component.VEvent
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
  private val eventMapper: MainEventMapper,
) : Runnable {

  override fun run(): Unit = runBlocking(Dispatchers.Default) {
    log.info { "Getting issues" }
    val issuesIterator = issuesGetter.issuesIterator()
    val vEventList: List<VEvent> = issuesIterator.asSequence()
      .map { async { eventMapper(it) } }
      .toList()
      .map { it.await() }
      .filter { result ->
        if (result.isFailure) {
          false
        } else {
          true
        }
      }
      .map { result -> result.getOrThrow() }
    log.info { vEventList }
  }
}
