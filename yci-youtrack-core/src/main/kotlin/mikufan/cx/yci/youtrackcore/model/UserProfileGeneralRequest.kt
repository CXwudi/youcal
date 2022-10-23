package mikufan.cx.yci.youtrackcore.model

import java.net.URI

/**
 * @date 2022-10-22
 * @author CX无敌
 */
data class UserProfileGeneralRequest(
  override val baseUri: URI,
  override val bearerToken: String,
  val id: String = "me",
  val fields: String,
) : BaseYouTrackApiRequest(baseUri, bearerToken)
