package mikufan.cx.yc.core.ical.util

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * @author CX无敌
 * 2023-01-03
 */
typealias YouTrackIssueJson = ObjectNode

fun YouTrackIssueJson.tryGetCustomField(fieldName: String, expectedType: String = ""): ObjectNode? {
  val customField = get("customFields").find { it["name"].asText() == fieldName } as ObjectNode?
  if (expectedType.isNotBlank() && customField != null && customField["\$type"].asText() != expectedType) {
    throw IllegalArgumentException("Custom field $fieldName is not of type $expectedType")
  }
  return customField
}

fun YouTrackIssueJson.getCustomFieldOrThrow(
  fieldName: String,
  expectedType: String = "",
  thrower: () -> Exception,
): ObjectNode {
  return tryGetCustomField(fieldName, expectedType) ?: throw thrower()
}

val YouTrackIssueJson.debugName: String
  get() = "Issue ${get("idReadable").asText()} ${get("summary").asText()}"
