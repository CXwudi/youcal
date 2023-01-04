package mikufan.cx.yc.core.ical.component

import com.fasterxml.jackson.databind.JsonNode
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.model.StringMappableVEventField
import mikufan.cx.yc.core.ical.model.StringMapping
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
import mikufan.cx.yc.core.ical.util.debugName
import mikufan.cx.yc.core.ical.util.getCustomField
import net.fortuna.ical4j.model.component.VEvent

/**
 * @author CX无敌
 * 2023-01-03
 */
class OtherFieldsSetter {
  fun doMapAndSet(event: VEvent, json: YouTrackIssueJson, otherMappings: OtherStringMappings) {
    log.info { "Mapping some string mappable fields for ${json.debugName}" }
    otherMappings.list.forEach { doMapAndSet(event, json, it) }
  }

  private fun doMapAndSet(event: VEvent, json: YouTrackIssueJson, otherMapping: StringMapping) {
    val (fromYouTrackFieldName, defaultValue, toVEventField) = otherMapping
    if (fromYouTrackFieldName.isNullOrBlank()) {
      tryMapDefaultValue(defaultValue, toVEventField, json, event, otherMapping)
    } else {
      val valueNode = tryGetFieldValueJsonNode(json, fromYouTrackFieldName)
      if (valueNode.isArray) {
        if (valueNode.isEmpty) {
          tryMapDefaultValue(defaultValue, toVEventField, json, event, otherMapping)
        } else {
          val valueStr = valueNode.map { it["name"].asText() }
            .filter { it.isNotBlank() && it != "null" }
            .joinToString(",")
          log.debug { "mapped array value $valueStr to ${toVEventField.name} for ${json.debugName}" }
          event.add(toVEventField.createProperty(valueStr))
        }
      } else if (valueNode.isNull) {
        tryMapDefaultValue(defaultValue, toVEventField, json, event, otherMapping)
      } else {
        val valueStr = valueNode["name"].asText()
        log.debug { "mapped value $valueStr to ${toVEventField.name} for ${json.debugName}" }
        event.add(toVEventField.createProperty(valueStr))
      }
    }
  }

  private fun tryMapDefaultValue(
    defaultValue: String?,
    toVEventField: StringMappableVEventField,
    json: YouTrackIssueJson,
    event: VEvent,
    otherMapping: StringMapping,
  ) {
    if (!defaultValue.isNullOrBlank()) {
      log.debug { "mapped default value $defaultValue to ${toVEventField.name} for ${json.debugName}" }
      event.add(toVEventField.createProperty(defaultValue))
    } else {
      log.warn { "This line shouldn't happen. json = $json, otherMapping = $otherMapping" }
    }
  }

  /**
   *
   * @param json ObjectNode
   * @param fieldName String
   * @return JsonNode can be either an [ObjectNode] representing the value
   * or an [ArrayNode] where the caller should manually map it to a string
   */
  private fun tryGetFieldValueJsonNode(json: YouTrackIssueJson, fieldName: String): JsonNode {
    val defaultField = json[fieldName]
    if (defaultField != null && !defaultField.isNull) return defaultField
    val customField = json.getCustomField(fieldName)
    return customField["value"]
  }
}

private val log = KInlineLogging.logger()
