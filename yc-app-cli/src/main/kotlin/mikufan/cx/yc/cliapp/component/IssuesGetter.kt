package mikufan.cx.yc.cliapp.component

import mikufan.cx.yc.apiclient.yt.api.issues.IssuesApi
import mikufan.cx.yc.cliapp.config.AlarmConfig
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.cliapp.config.EnabledAlarmConfig
import mikufan.cx.yc.cliapp.config.SearchConfig
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.util.YouTrackDefaultDateTime
import mikufan.cx.yc.core.ical.util.YouTrackIssueJson
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
  private val alarmConfig: AlarmConfig,
  private val otherStringMappings: OtherStringMappings,
) {

  fun issuesIterator(): Iterator<YouTrackIssueJson> {
    val (youtrackFieldNames, customFieldNames) = getFields()
    return issuesApi.getIssuesLazily(
      query = searchConfig.query,
      fields = listOf(
        "id",
        "idReadable",
        "summary",
        "description",
        "customFields(name,id,value(name,id))"
      ) + youtrackFieldNames,
      customFields = customFieldNames,
      pageSize = searchConfig.pageSize,
    )
  }

  internal fun getFields(): Pair<List<String>, List<String>> {
    val youtrackFieldNames = mutableListOf<String>()
    val customFieldNames = mutableListOf<String>()
    dateTimeConfig.fieldNames.forEach { name ->
      if (YouTrackDefaultDateTime.isYouTrackDefaultDateTimeField(name)) {
        youtrackFieldNames.add(name)
      } else {
        customFieldNames.add(name)
      }
    }
    if (alarmConfig is EnabledAlarmConfig) {
      val periodFieldName = alarmConfig.periodFieldName
      if (periodFieldName.isNotBlank()) {
        customFieldNames.add(periodFieldName)
      }
    }
    otherStringMappings.list
      .map { (youtrackFieldName, _, _) -> youtrackFieldName }
      .filter { it.isNotBlank() }
      .forEach { fieldName ->
        customFieldNames.add(fieldName)
      }
    return youtrackFieldNames to customFieldNames
  }
}
