package mikufan.cx.yci.youtrackcore.api

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yci.youtrackcore.model.IssueListRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

/**
 * @date 2022-10-09
 * @author CX无敌
 */
@Service
class YouTrackIssueListApi(
  private val restTemplate: RestTemplate,
) {

  fun readIssueList(request: IssueListRequest): List<ObjectNode> = readIssueListLazily(request).asSequence().toList()

  /**
   *
   * @param request IssueListRequest
   * @return Iterator<ObjectNode> An iterator that will read the issue list on demand. This iterator is not thread safe
   */
  fun readIssueListLazily(request: IssueListRequest): Iterator<ObjectNode> = object : Iterator<ObjectNode> {
    val baseQueryUrl = buildBaseQueryUrl(request)
    val top = request.pageSize
    var skip = 0
    val buffer = ArrayDeque<ObjectNode>(top)
    var stillHasMore = true

    /**
     * Returns `true` if the iteration has more elements.
     */
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
        log.debug { "reading $baseQueryUrl starting from $skip of amount $top" }
        val partialList = doApiCall(baseQueryUrl, request.bearerToken, top, skip)
        buffer.addAll(partialList.map { it as ObjectNode })
        skip += top
        stillHasMore = partialList.size() == top
      } else {
        log.debug { "no more issue to read from $baseQueryUrl" }
      }
    }
  }

  internal fun buildBaseQueryUrl(request: IssueListRequest): URI {
    val (baseUri, _, query, fields, customFields) = request
    val queryUrl = UriComponentsBuilder.fromUri(baseUri)
      .pathSegment("issues")
      .queryParam("query", query)
      .queryParam("fields", "${fields.joinToString(",")},customFields(name,value(name))")
      .apply {
        customFields.forEach { customField ->
          queryParam("customFields", customField)
        }
      }
      .build().toUri()
    log.debug { "base query url: $queryUrl" }
    return queryUrl
  }

  internal fun doApiCall(baseQueryUri: URI, bearerToken: String, top: Int, skip: Int): ArrayNode {
    val queryUri = UriComponentsBuilder.fromUri(baseQueryUri)
      .queryParam("\$top", top)
      .queryParam("\$skip", skip)
      .build(true).toUri()
    log.debug { "query url: $queryUri" }
    val headers = HttpHeaders().apply {
      if (bearerToken.isNotBlank()) {
        add(HttpHeaders.AUTHORIZATION, "Bearer $bearerToken")
      }
      add(HttpHeaders.ACCEPT, "application/json")
    }
    val request = RequestEntity<String>(headers, HttpMethod.GET, queryUri)
    val response = restTemplate.exchange<ArrayNode>(request)
    return response.body ?: throw IllegalStateException("response body is null from $queryUri")
  }
}

private val log = KInlineLogging.logger()
