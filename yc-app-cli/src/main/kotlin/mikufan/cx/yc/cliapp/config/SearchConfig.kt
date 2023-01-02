package mikufan.cx.yc.cliapp.config

import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @author CX无敌
 * 2023-01-01
 */
@ConfigurationProperties(prefix = "cliapp.search")
@Validated
data class SearchConfig(
  /** the query to search for issues */
  val query: String = "",
  /** the max number of issues to return in one page */
  @get:Positive
  val pageSize: Int,
)
