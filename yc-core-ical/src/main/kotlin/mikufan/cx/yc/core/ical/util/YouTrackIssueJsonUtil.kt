package mikufan.cx.yc.core.ical.util

import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.yc.core.ical.model.exception.MappingException

/**
 * @author CX无敌
 * 2023-01-03
 */
typealias YouTrackIssueJson = ObjectNode

fun YouTrackIssueJson.getCustomField(fieldName: String, expectedType: String = ""): ObjectNode {
  val customField: ObjectNode = get("customFields").find { it["name"].asText() == fieldName } as ObjectNode?
    ?: throw MappingException("Cannot find custom field $fieldName in $this")
  if (expectedType.isNotBlank() && customField["\$type"].asText() != expectedType) {
    throw MappingException("Custom field $fieldName is not of type $expectedType")
  }
  return customField
}
