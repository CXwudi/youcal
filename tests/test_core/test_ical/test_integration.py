"""Integration tests using real YouTrack JSON data.

These tests are migrated from the Kotlin tests in yc-core-ical module.
"""

from datetime import date, timedelta
from zoneinfo import ZoneInfo

import pytest

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical import (
  AlarmMapper,
  AlarmSetting,
  CalendarBuilder,
  DateTimeFieldSetter,
  EventMapper,
  IssueMappingConfig,
  MappingException,
  OneDayDateTimeFieldInfo,
  OtherFieldsSetter,
  OtherStringMappings,
  StringMapping,
  VEventField,
)


class TestAlarmMapperWithRealData:
  """Tests for AlarmMapper using real YouTrack JSON data."""

  def test_null_alarm_setting_returns_none(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test that null alarm setting returns None for all issues."""
    mapper = AlarmMapper()
    for issue in issues_with_all_fields:
      alarm = mapper.create_alarm(issue, None)
      assert alarm is None, f"Expected None for {issue.id_readable}"

  def test_default_duration_only(self, issues_with_all_fields: list[YouTrackIssue]):
    """Test alarm with only default duration (no field specified)."""
    mapper = AlarmMapper()
    setting = AlarmSetting(
      duration_field_name="",
      is_negative_duration=True,
      default_shift_duration=timedelta(minutes=30),
    )
    for issue in issues_with_all_fields:
      alarm = mapper.create_alarm(issue, setting)
      assert alarm is not None, f"Expected alarm for {issue.id_readable}"
      assert alarm.get("action") == "DISPLAY"
      # Trigger should be -30 minutes
      trigger = alarm.get("trigger")
      assert trigger is not None

  def test_specified_duration_field_with_default(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test alarm with specified duration field and default fallback."""
    mapper = AlarmMapper()
    setting = AlarmSetting(
      duration_field_name="Estimation",
      is_negative_duration=True,
      default_shift_duration=timedelta(minutes=30),
    )
    for issue in issues_with_all_fields:
      alarm = mapper.create_alarm(issue, setting)
      assert alarm is not None, f"Expected alarm for {issue.id_readable}"
      # All issues should have alarm (either from field or default)

  def test_specified_duration_field_only(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test alarm with specified duration field only (no default).

    AL-10 has no Estimation field value, so it should return None.
    """
    mapper = AlarmMapper()
    setting = AlarmSetting(
      duration_field_name="Estimation",
      is_negative_duration=True,
      default_shift_duration=None,
    )
    for issue in issues_with_all_fields:
      alarm = mapper.create_alarm(issue, setting)
      if issue.id_readable == "AL-10":
        # AL-10 has null Estimation, should return None
        assert alarm is None, f"Expected None for {issue.id_readable}"
      else:
        # Other issues have Estimation value
        assert alarm is not None, f"Expected alarm for {issue.id_readable}"


class TestDateTimeFieldSetterWithRealData:
  """Tests for DateTimeFieldSetter using real YouTrack JSON data."""

  def test_set_dtstart_from_due_date(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test setting DTSTART from Due Date field."""
    from icalendar import Event

    setter = DateTimeFieldSetter()
    datetime_info = OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("Canada/Eastern"),
    )

    for issue in issues_with_all_fields:
      event = Event()
      try:
        setter.set_datetime(event, issue, datetime_info)
        dtstart = event.get("dtstart")
        assert dtstart is not None, f"Expected DTSTART for {issue.id_readable}"
        # Due Date is 1673179200000 = 2023-01-08 in UTC
        # In Canada/Eastern that's still 2023-01-08
        assert isinstance(dtstart.dt, date)
      except MappingException:
        # Some issues might not have Due Date
        pass

  def test_missing_date_field_raises_exception(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test that missing date field raises MappingException."""
    from icalendar import Event

    setter = DateTimeFieldSetter()
    datetime_info = OneDayDateTimeFieldInfo(
      field_name="Nonexistent Field",
      zone_id=ZoneInfo("UTC"),
    )

    for issue in issues_with_all_fields:
      event = Event()
      with pytest.raises(MappingException):
        setter.set_datetime(event, issue, datetime_info)


class TestOtherFieldsSetterWithRealData:
  """Tests for OtherFieldsSetter using real YouTrack JSON data."""

  def test_mapping_with_defaults(
    self, issues_with_missing_values: list[YouTrackIssue]
  ):
    """Test mapping fields with default values for missing data."""
    from icalendar import Event

    setter = OtherFieldsSetter()
    mappings = OtherStringMappings(
      mappings=[
        StringMapping(
          from_field_name="State",
          default_value="Unresolved",
          to_vevent_field=VEventField.STATUS,
        ),
        StringMapping(
          from_field_name="Assignee",
          default_value="Unassigned",
          to_vevent_field=VEventField.ATTENDEE,
        ),
        StringMapping(
          from_field_name="",
          default_value="OPAQUE",
          to_vevent_field=VEventField.TRANSP,
        ),
        StringMapping(
          from_field_name="Submodule",
          default_value="No module",
          to_vevent_field=VEventField.CATEGORIES,
        ),
      ]
    )

    for issue in issues_with_missing_values:
      event = Event()
      setter.set_fields(event, issue, mappings)

      # TRANSP should always be set (default value, no field)
      assert event.get("transp") is not None

      # Check that some properties are set
      # (exact values depend on whether field has value or uses default)


class TestEventMapperIntegration:
  """Integration tests for EventMapper using real YouTrack JSON data."""

  def test_map_issues_without_alarm(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test mapping issues to events without alarm."""
    mapper = EventMapper()
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("Canada/Eastern"),
      ),
      alarm_setting=None,
      other_mappings=OtherStringMappings(mappings=[]),
    )

    for issue in issues_with_all_fields:
      try:
        event = mapper.map_issue(issue, config)
        assert event.get("uid") == issue.id_readable
        assert issue.id_readable in str(event.get("summary"))
        # No alarm should be present
        alarms = [c for c in event.subcomponents if c.name == "VALARM"]
        assert len(alarms) == 0
      except MappingException:
        # Some issues might not have Due Date
        pass

  def test_map_issues_with_alarm(self, issues_with_all_fields: list[YouTrackIssue]):
    """Test mapping issues to events with alarm."""
    mapper = EventMapper()
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("Canada/Eastern"),
      ),
      alarm_setting=AlarmSetting(
        duration_field_name="Estimation",
        is_negative_duration=True,
        default_shift_duration=timedelta(minutes=15),
      ),
      other_mappings=OtherStringMappings(mappings=[]),
    )

    for issue in issues_with_all_fields:
      try:
        event = mapper.map_issue(issue, config)
        assert event.get("uid") == issue.id_readable
        # Alarm should be present (either from field or default)
        alarms = [c for c in event.subcomponents if c.name == "VALARM"]
        assert len(alarms) == 1, f"Expected 1 alarm for {issue.id_readable}"
      except MappingException:
        # Some issues might not have Due Date
        pass

  def test_map_issues_with_alarm_and_other_fields(
    self, issues_with_missing_values: list[YouTrackIssue]
  ):
    """Test mapping issues with alarm and other field mappings."""
    mapper = EventMapper()
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("Canada/Eastern"),
      ),
      alarm_setting=AlarmSetting(
        duration_field_name="Estimation",
        is_negative_duration=True,
        default_shift_duration=timedelta(minutes=15),
      ),
      other_mappings=OtherStringMappings(
        mappings=[
          StringMapping(
            from_field_name="State",
            default_value="Unresolved",
            to_vevent_field=VEventField.STATUS,
          ),
          StringMapping(
            from_field_name="Assignee",
            default_value="Unassigned",
            to_vevent_field=VEventField.ATTENDEE,
          ),
          StringMapping(
            from_field_name="",
            default_value="OPAQUE",
            to_vevent_field=VEventField.TRANSP,
          ),
          StringMapping(
            from_field_name="Submodule",
            default_value="No module",
            to_vevent_field=VEventField.CATEGORIES,
          ),
        ]
      ),
    )

    for issue in issues_with_missing_values:
      try:
        event = mapper.map_issue(issue, config)
        assert event.get("uid") == issue.id_readable

        # Check basic properties
        assert event.get("summary") is not None
        assert event.get("dtstart") is not None

        # Check other mapped fields
        assert event.get("transp") is not None  # Always set (default)

      except MappingException as e:
        # Expected for issues without Due Date
        print(f"MappingException for {issue.id_readable}: {e}")


class TestCalendarBuilderIntegration:
  """Integration tests for CalendarBuilder."""

  def test_build_calendar_from_issues(
    self, issues_with_all_fields: list[YouTrackIssue]
  ):
    """Test building a complete calendar from issues."""
    mapper = EventMapper()
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("Canada/Eastern"),
      ),
      alarm_setting=AlarmSetting(
        duration_field_name="Estimation",
        is_negative_duration=True,
        default_shift_duration=timedelta(minutes=15),
      ),
    )

    # Map issues to events
    events = []
    for issue in issues_with_all_fields:
      try:
        event = mapper.map_issue(issue, config)
        events.append(event)
      except MappingException:
        pass

    # Build calendar
    builder = CalendarBuilder()
    calendar = builder.build(events)

    # Verify calendar properties
    assert calendar.get("prodid") is not None
    assert calendar.get("version") == "2.0"

    # Verify events are included
    vevent_components = [c for c in calendar.subcomponents if c.name == "VEVENT"]
    assert len(vevent_components) == len(events)

    # Serialize to iCal string
    ical_string = builder.to_ical_string(calendar)
    assert "BEGIN:VCALENDAR" in ical_string
    assert "BEGIN:VEVENT" in ical_string
    assert "END:VCALENDAR" in ical_string
