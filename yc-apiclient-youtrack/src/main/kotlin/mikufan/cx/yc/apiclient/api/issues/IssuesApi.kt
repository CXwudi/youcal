package mikufan.cx.yc.apiclient.api.issues

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * @author CX无敌
 * 2022-11-29
 */
// you can't make it private nor can proxy class make method private
@HttpExchange("issues")
@JvmDefaultWithCompatibility
interface IssuesApi {

  @GetExchange
  fun getIssues(
    @RequestParam("query") query: String,
    @RequestParam("fields") fields: String,
    @RequestParam("customFields") customFields: List<String>,
    @RequestParam("\$skip") skip: Int,
    @RequestParam("\$top") top: Int,
  ): ArrayNode

  fun getIssuesLazily(
    query: String,
    fields: List<String>,
    customFields: List<String>,
    startAt: Int = 0,
    pageSize: Int,
  ): Iterator<ObjectNode> = object : Iterator<ObjectNode> {
    var skip = startAt
    val buffer = ArrayDeque<ObjectNode>(pageSize)
    var stillHasMore = true

    override fun hasNext(): Boolean {
      if (buffer.isEmpty()) {
        readNewPage()
      }
      return buffer.isNotEmpty()
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): ObjectNode {
      if (buffer.isEmpty()) {
        readNewPage()
      }
      return buffer.removeAt(0)
    }

    private fun readNewPage() {
      if (stillHasMore) {
        log.debug { "reading issue list $query starting from $skip of amount $pageSize" }
        val partialList = getIssues(query, fields.joinToString(","), customFields, skip, pageSize)
        buffer.addAll(partialList.map { it as ObjectNode })
        skip += pageSize
        stillHasMore = partialList.size() == pageSize
      } else {
        log.debug { "no more issue to read from $query" }
      }
    }
  }
}

private val log = KInlineLogging.logger()
