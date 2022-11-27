package mikufan.cx.yci.youtrackcore.api.users.id.profiles.general

import com.fasterxml.jackson.databind.JsonNode
import mikufan.cx.yci.youtrackcore.api.YouTrackApiClient
import mikufan.cx.yci.youtrackcore.model.BaseYouTrackApiRequest
import mikufan.cx.yci.youtrackcore.model.users.id.profiles.general.GeneralUserProfileGetRequest
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.ZoneId

/**
 * @date 2022-10-22
 * @author CX无敌
 */
@Service
class GeneralUserProfileApi(
  private val apiClient: YouTrackApiClient,
) {

  fun getZoneIdOfUser(userId: String = "me", youTrackApiRequest: BaseYouTrackApiRequest): ZoneId {
    val request = GeneralUserProfileGetRequest(
      youTrackApiRequest.baseUri,
      youTrackApiRequest.bearerToken,
      userId,
      "timezone(id,offset,presentation)"
    )
    val zoneIdStr = getProfile(request)["timezone"]["id"].asText()
    return ZoneId.of(zoneIdStr)
  }

  fun getProfile(request: GeneralUserProfileGetRequest): JsonNode {
    val uri = buildUri(request)
    return apiClient.get(uri, request.bearerToken)
  }

  private fun buildUri(request: GeneralUserProfileGetRequest): URI =
    UriComponentsBuilder.fromUri(request.baseUri)
      .pathSegment("users", request.id, "profiles", "general")
      .queryParam("fields", request.fields)
      .build(true).toUri()
}
