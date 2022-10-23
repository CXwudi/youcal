package mikufan.cx.yci.youtrackcore.api

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI

/**
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
