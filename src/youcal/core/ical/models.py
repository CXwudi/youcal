"""Pydantic models for iCalendar mapping configuration."""

from datetime import timedelta
from enum import Enum
from zoneinfo import ZoneInfo

from pydantic import BaseModel, ConfigDict, model_validator


class EventType(str, Enum):
  """Type of calendar event to generate."""

  ONE_DAY_EVENT = "one_day_event"
  # DURATION_DATETIME_EVENT = "duration_datetime_event"  # TODO: future


class ShiftBasedOn(str, Enum):
  """Reference point for alarm trigger timing."""

  START = "start"
  END = "end"


class VEventField(str, Enum):
  """Supported VEvent properties for string mapping."""

  ATTENDEE = "attendee"
  STATUS = "status"
  TRANSP = "transp"
  CATEGORIES = "categories"


class OneDayDateTimeFieldInfo(BaseModel):
  """Configuration for mapping a YouTrack date field to an all-day event.

  Attributes:
    field_name: The YouTrack field name containing the date (e.g., "Due Date", "created")
    zone_id: Timezone for converting epoch milliseconds to local date
  """

  field_name: str
  zone_id: ZoneInfo

  model_config = ConfigDict(arbitrary_types_allowed=True)


class AlarmSetting(BaseModel):
  """Configuration for creating VAlarm reminders.

  At least one of duration_field_name or default_shift_duration must be provided.

  Attributes:
    duration_field_name: YouTrack period field name for alarm timing (optional)
    is_negative_duration: If True, alarm triggers before the event; if False, after
    default_shift_duration: Fallback duration if field is missing/empty
    shift_based_on: Whether alarm is relative to event START or END
  """

  duration_field_name: str = ""
  is_negative_duration: bool = True
  default_shift_duration: timedelta | None = None
  shift_based_on: ShiftBasedOn = ShiftBasedOn.START

  @model_validator(mode="after")
  def validate_at_least_one_duration(self) -> "AlarmSetting":
    """Ensure at least one duration source is specified."""
    if not self.duration_field_name and self.default_shift_duration is None:
      raise ValueError(
        "Either duration_field_name or default_shift_duration must be specified"
      )
    return self


class StringMapping(BaseModel):
  """Configuration for mapping a YouTrack field to a VEvent string property.

  At least one of from_field_name or default_value must be provided.

  Attributes:
    from_field_name: YouTrack field name to extract value from (optional)
    default_value: Fallback value if field is missing/empty
    to_vevent_field: Target VEvent property
  """

  from_field_name: str = ""
  default_value: str = ""
  to_vevent_field: VEventField

  @model_validator(mode="after")
  def validate_at_least_one_value(self) -> "StringMapping":
    """Ensure at least one value source is specified."""
    if not self.from_field_name and not self.default_value:
      raise ValueError("Either from_field_name or default_value must be specified")
    return self


class OtherStringMappings(BaseModel):
  """Container for multiple string field mappings.

  Attributes:
    mappings: List of StringMapping configurations
  """

  mappings: list[StringMapping] = []


class IssueMappingConfig(BaseModel):
  """Complete configuration for mapping a YouTrack issue to a VEvent.

  Attributes:
    event_type: Type of event (currently only ONE_DAY_EVENT supported)
    datetime_field_info: Configuration for the date/time field
    alarm_setting: Optional alarm configuration (None disables alarms)
    other_mappings: Additional field mappings for attendee, status, etc.
  """

  event_type: EventType = EventType.ONE_DAY_EVENT
  datetime_field_info: OneDayDateTimeFieldInfo
  alarm_setting: AlarmSetting | None = None
  other_mappings: OtherStringMappings = OtherStringMappings()
