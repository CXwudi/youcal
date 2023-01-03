package mikufan.cx.yc.core.ical.util

/**
 * @author CX无敌
 * 2023-01-01
 */

object YouTrackDefaultDateTime {
  const val CREATED = "created"
  const val UPDATED = "updated"
  const val RESOLVED = "resolved"

  fun isYouTrackDefaultDateTimeField(field: String): Boolean {
    return field == CREATED || field == UPDATED || field == RESOLVED
  }
}
