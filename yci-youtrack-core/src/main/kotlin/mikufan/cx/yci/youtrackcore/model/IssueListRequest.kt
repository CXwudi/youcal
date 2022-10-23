package mikufan.cx.yci.youtrackcore.model

import java.net.URI

/**
 * A generic request over the YouTrack API GET /issues endpoint.
 * @date 2022-10-09
 * @author CX无敌
 */
data class IssueListRequest(
  /**
   * the uri of the api entry point of the youtrack instance
   *
   * do not just put the domain name, but the full url with `/api`
   *
   * e.g. https://youtrack.jetbrains.com/api/
   */
  val baseUri: URI,
  val bearerToken: String,
  val query: String,
  val fields: List<String>,
  val customFields: List<String> = emptyList(),
  val pageSize: Int = 40,
)
