package mikufan.cx.yc.cliapp.component

import com.fasterxml.jackson.databind.node.ObjectNode
import mikufan.cx.yc.apiclient.yt.api.issues.IssuesApi
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.cliapp.config.SearchConfig
import mikufan.cx.yc.core.ical.model.DurationEventFields
import mikufan.cx.yc.core.ical.model.OneDayEventField
import mikufan.cx.yc.core.ical.model.YouTrackDefaultDateTime
import org.springframework.stereotype.Component

/**
 * @author CX无敌
 * 2023-01-01
 */
@Component
class IssuesGetter(
  private val issuesApi: IssuesApi,
  private val searchConfig: SearchConfig,
  private val dateTimeConfig: DateTimeConfig,
) {

  fun issuesIterator(): Iterator<ObjectNode> {
    val (youtrackFields, customFields) = getFields(dateTimeConfig)
    return issuesApi.getIssuesLazily(
      query = searchConfig.query,
      fields = listOf("id", "idReadable", "summary", "description") + youtrackFields,
      customFields = customFields,
      pageSize = searchConfig.pageSize,
    )
  }

  internal fun getFields(dateTimeConfig: DateTimeConfig): Pair<List<String>, List<String>> {
    val dateTimeFields = dateTimeConfig.fields
    val youtrackFields = mutableListOf<String>()
    val customFields = mutableListOf<String>()
    when (dateTimeFields) {
      is OneDayEventField -> {
        val field = dateTimeFields.field
        if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(field)) {
          youtrackFields.add(field)
        } else {
          customFields.add(field)
        }
      }

      is DurationEventFields -> {
        listOf(dateTimeFields.startField, dateTimeFields.endField).forEach { field ->
          if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(field)) {
            youtrackFields.add(field)
          } else {
            customFields.add(field)
          }
        }
      }
    }
    return youtrackFields to customFields
  }
}
