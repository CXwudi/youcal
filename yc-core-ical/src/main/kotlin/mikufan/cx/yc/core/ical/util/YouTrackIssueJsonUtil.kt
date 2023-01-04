package mikufan.cx.yc.core.ical.util

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * @author CX无敌
 * 2023-01-03
 */
typealias YouTrackIssueJson = ObjectNode

fun YouTrackIssueJson.getCustomField(fieldName: String, expectedType: String = ""): ObjectNode {
  val customField: ObjectNode = get("customFields").find { it["name"].asText() == fieldName } as ObjectNode?
    ?: throw IllegalArgumentException("Cannot find custom field $fieldName in $this")
  if (expectedType.isNotBlank() && customField["\$type"].asText() != expectedType) {
    throw IllegalArgumentException("Custom field $fieldName is not of type $expectedType")
  }
  return customField
}

val YouTrackIssueJson.debugName: String
  get() = "Issue ${get("idReadable").asText()} ${get("summary").asText()}"
