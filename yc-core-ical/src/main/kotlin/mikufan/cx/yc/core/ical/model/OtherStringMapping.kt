package mikufan.cx.yc.core.ical.model

import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Categories
import net.fortuna.ical4j.model.property.Status
import net.fortuna.ical4j.model.property.Transp

/**
 * @author CX无敌
 * 2023-01-02
 */
data class OtherStringMappings(
  val list: List<StringMapping>,
)

data class StringMapping(
  val fromYouTrackFieldName: String?,
  val defaultValue: String?,
  val toVEventFieldName: StringMappableVEventField,
) {
  init {
    require(fromYouTrackFieldName != null || defaultValue != null) {
      "Either fromYouTrackFieldName or defaultValue must be non-null"
    }
  }
}

enum class StringMappableVEventField(val createProperty: (String) -> Property) {
  ATTENDEE({ Attendee(it.uppercase()) }),
  STATUS({ Status(it.uppercase()) }),
  TRANSP({ Transp(it.uppercase()) }),
  CATEGORIES({ Categories(it.uppercase()) }),
}
