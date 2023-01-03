package mikufan.cx.yc.core.ical.model

/**
 * @author CX无敌
 * 2023-01-02
 */
data class OtherStringMapping(
  val fromYouTrackFieldName: String?,
  val defaultValue: String?,
  val toICalFieldName: String,
) {
  init {
    require(fromYouTrackFieldName != null || defaultValue != null) {
      "Either fromYouTrackFieldName or defaultValue must be non-null"
    }
  }
}
