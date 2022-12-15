package mikufan.cx.yc.apiclient.yt.api.users

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import java.time.ZoneId

/**
 * @author CX无敌
 * 2022-11-29
 */
@HttpExchange("users")
@JvmDefaultWithCompatibility
interface UsersApi {

  @GetExchange("{id}/profiles/general")
  fun getGeneralProfile(
    @PathVariable("id") id: String = "me",
    @RequestParam("fields") fields: String,
  ): ObjectNode

  fun getZoneIdOfUser(
    id: String = "me",
  ): ZoneId {
    val rsp = getGeneralProfile(id, "timezone(id,offset,presentation)")
    val zoneIdStr = rsp["timezone"]["id"].asText()
    return ZoneId.of(zoneIdStr)
  }
}
