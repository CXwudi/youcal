package mikufan.cx.yc.core.ical.model

/**
 * @author CX无敌
 * 2023-01-01
 */

sealed interface EventDateTimeField

data class OneEventField(
  val field: String,
) : EventDateTimeField

data class StartEndEventFields(
  val startField: String,
  val endField: String,
) : EventDateTimeField
