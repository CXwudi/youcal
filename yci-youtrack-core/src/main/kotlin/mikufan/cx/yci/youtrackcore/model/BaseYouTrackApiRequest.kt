package mikufan.cx.yci.youtrackcore.model

import java.net.URI

/**
 * @date 2022-10-22
 * @author CX无敌
 */
open class BaseYouTrackApiRequest(
  /**
   * the uri of the api entry point of the youtrack instance
   *
   * do not just put the domain name, but the full url with `/api`
   *
   * e.g. https://youtrack.jetbrains.com/api/
   */
  open val baseUri: URI,
  open val bearerToken: String,
)
