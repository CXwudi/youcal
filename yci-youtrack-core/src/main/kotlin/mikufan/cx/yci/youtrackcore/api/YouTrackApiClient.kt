package mikufan.cx.yci.youtrackcore.api

import com.fasterxml.jackson.databind.JsonNode
import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI

/**
 * The generic api that calls the youtrack api with specific uri and bearer token
 *
 * and return the response as your choice, in this project, it is usually a [JsonNode]
 * @date 2022-10-22
 * @author CX无敌
 */
@Service
class YouTrackApiClient(
  val restTemplate: RestTemplate,
) {

  companion object {
    @JvmStatic
    val log = KInlineLogging.logger()
  }

  /**
   * Perform a get request to the youtrack api
   * @param uri URI the URI of the api
   * @param bearerToken String the bearer token of the user
   * @return O the response of the api, can be any POJO or [JsonNode]
   */
  final inline fun <reified O> get(uri: URI, bearerToken: String): O {
    log.debug { "GET $uri" }
    val headers = HttpHeaders().apply {
      if (bearerToken.isNotBlank()) {
        add(HttpHeaders.AUTHORIZATION, "Bearer $bearerToken")
      }
      add(HttpHeaders.ACCEPT, "application/json")
    }
    val req = RequestEntity.get(uri).headers(headers).build()
    val rsp = restTemplate.exchange<O>(req)
    return rsp.body ?: throw IllegalStateException("response body is null")
  }
}
