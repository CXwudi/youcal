package mikufan.cx.yc.cliapp.config

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import mikufan.cx.yc.cliapp.config.validation.IsValidStringMapping
import mikufan.cx.yc.core.ical.model.OtherStringMappings
import mikufan.cx.yc.core.ical.model.StringMappableVEventField
import mikufan.cx.yc.core.ical.model.StringMapping
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

/**
 * @author CX无敌
 * 2023-01-04
 */
@ConfigurationProperties(prefix = "cliapp.other-mapping")
@Validated
data class OtherMappingConfigRaw(
  val list: List<@Valid StringMappingRaw> = emptyList(),
)

@IsValidStringMapping
data class StringMappingRaw(
  val fromFieldName: String = "",
  val defaultValue: String = "",
  @get:NotNull
  val toFieldName: StringMappableVEventField,
)

@Configuration
class OtherMappingConfigConfiguration {

  @Bean
  fun otherStringMappings(raw: OtherMappingConfigRaw) = OtherStringMappings(
    raw.list.map {
      StringMapping(
        fromYouTrackFieldName = it.fromFieldName,
        defaultValue = it.defaultValue,
        toVEventFieldName = it.toFieldName,
      )
    }
  )
}
