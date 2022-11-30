package mikufan.cx.yc.icalcore.model

sealed interface DateFieldName {
  companion object {
    const val CREATED: String = "created"
    const val UPDATED: String = "updated"
    const val RESOLVED: String = "resolved"

    fun isBuildInDateField(fieldName: String): Boolean {
      return fieldName == CREATED || fieldName == UPDATED || fieldName == RESOLVED
    }
  }
}

data class WholeDayEventDateFieldName(val fieldName: String) : DateFieldName

data class TimeEventDateFieldName(val startDateFieldName: String, val endDateFieldName: String) : DateFieldName
