package mikufan.cx.yci.youtrackcore.model

import java.net.URI

/**
 * A generic request over the YouTrack API GET /issues endpoint.
 * @date 2022-10-09
 * @author CX无敌
 */
data class IssueListRequest(
  override val baseUri: URI,
  override val bearerToken: String,
  val query: String,
  val fields: List<String>,
  val customFields: List<String> = emptyList(),
  val pageSize: Int = 40,
) : BaseYouTrackApiRequest(baseUri, bearerToken)
