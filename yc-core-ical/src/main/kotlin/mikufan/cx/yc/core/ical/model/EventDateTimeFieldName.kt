package mikufan.cx.yc.core.ical.model

/**
 * @author CX无敌
 * 2023-01-01
 */

sealed interface EventDateTimeFieldName

data class OneFieldName(
  val fieldName: String,
) : EventDateTimeFieldName

data class StartDurationFieldNames(
  val startFieldName: String,
  val durationFieldName: String,
) : EventDateTimeFieldName

data class StartEndFieldNames(
  val startFieldName: String,
  val endFieldName: String,
) : EventDateTimeFieldName
