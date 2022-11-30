package mikufan.cx.yc.icalcore.model

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * @date 2022-10-10
 * @author CX无敌
 */
data class InputYouTrackIssues(
  val issues: Iterator<ObjectNode>,
  val dateFieldName: DateFieldName,
  val timeZone: String,
)
