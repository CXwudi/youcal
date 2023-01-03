package mikufan.cx.yc.core.ical.model

/**
 * @author CX无敌
 * 2023-01-01
 */
enum class EventType {
  /**
   * The event is a one-day event, with only one field of the date
   */
  ONE_DAY_EVENT,

  /**
   * The event is a duration event, with precision of time.
   */
  DURATION_DATETIME_EVENT,

  // TODO: the start end date time event, start end date event
}
