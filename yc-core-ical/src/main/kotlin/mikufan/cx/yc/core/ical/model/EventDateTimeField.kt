package mikufan.cx.yc.core.ical.model

/**
 * @author CX无敌
 * 2023-01-01
 */

sealed interface EventDateTimeField

data class OneDayEventField(
  val field: String,
) : EventDateTimeField

data class DurationEventFields(
  val startField: String,
  val endField: String,
) : EventDateTimeField
