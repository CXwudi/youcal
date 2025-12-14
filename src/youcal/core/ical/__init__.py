"""iCalendar generation and mapping module.

This module provides functionality to convert YouTrack issues into
iCalendar format. It supports:

- All-day events from date fields
- Alarm/reminder creation from duration fields
- Custom field mapping to VEvent properties (STATUS, CATEGORIES, etc.)
- Calendar assembly with timezone support

Example:
  ```python
  from zoneinfo import ZoneInfo
  from youcal.core.ical import EventMapper, CalendarBuilder, IssueMappingConfig
  from youcal.core.ical.models import OneDayDateTimeFieldInfo

  # Configure mapping
  config = IssueMappingConfig(
    datetime_field_info=OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("America/Toronto"),
    ),
  )

  # Map issues to events
  mapper = EventMapper()
  events = [mapper.map_issue(issue, config) for issue in issues]

  # Build calendar
  builder = CalendarBuilder()
  calendar = builder.build(events)
  ics_content = builder.to_ical_string(calendar)
  ```
"""

from youcal.core.ical.alarm_mapper import AlarmMapper
from youcal.core.ical.calendar_builder import CalendarBuilder
from youcal.core.ical.datetime_setter import DateTimeFieldSetter
from youcal.core.ical.exceptions import MappingException
from youcal.core.ical.field_setter import OtherFieldsSetter
from youcal.core.ical.mapper import EventMapper
from youcal.core.ical.models import (
  AlarmSetting,
  EventType,
  IssueMappingConfig,
  OneDayDateTimeFieldInfo,
  OtherStringMappings,
  ShiftBasedOn,
  StringMapping,
  VEventField,
)

__all__ = [
  # Main classes
  "EventMapper",
  "CalendarBuilder",
  "DateTimeFieldSetter",
  "AlarmMapper",
  "OtherFieldsSetter",
  # Configuration models
  "IssueMappingConfig",
  "OneDayDateTimeFieldInfo",
  "AlarmSetting",
  "StringMapping",
  "OtherStringMappings",
  # Enums
  "EventType",
  "ShiftBasedOn",
  "VEventField",
  # Exceptions
  "MappingException",
]
