package mikufan.cx.yci.youtrackcore.model.issues

import mikufan.cx.yci.youtrackcore.model.BaseYouTrackApiRequest
import java.net.URI

/**
 * A generic request over the YouTrack API GET /issues endpoint.
 * @date 2022-10-09
 * @author CX无敌
 */
data class IssuesGetRequest(
  override val baseUri: URI,
  override val bearerToken: String,
  val query: String,
  val fields: List<String>,
  val customFields: List<String> = emptyList(),
  val pageSize: Int = 40,
) : BaseYouTrackApiRequest(baseUri, bearerToken)
