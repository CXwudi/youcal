"""Tests for iCalendar configuration models."""

from datetime import timedelta
from zoneinfo import ZoneInfo

import pytest

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


class TestOneDayDateTimeFieldInfo:
  """Tests for OneDayDateTimeFieldInfo model."""

  def test_create_with_valid_data(self):
    """Test creating a valid OneDayDateTimeFieldInfo."""
    info = OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("UTC"),
    )
    assert info.field_name == "Due Date"
    assert info.zone_id == ZoneInfo("UTC")

  def test_create_with_different_timezone(self):
    """Test creating with a different timezone."""
    info = OneDayDateTimeFieldInfo(
      field_name="Start Date",
      zone_id=ZoneInfo("America/Toronto"),
    )
    assert info.zone_id.key == "America/Toronto"


class TestAlarmSetting:
  """Tests for AlarmSetting model."""

  def test_create_with_duration_field(self):
    """Test creating alarm setting with duration field."""
    setting = AlarmSetting(
      duration_field_name="Reminder",
      is_negative_duration=True,
    )
    assert setting.duration_field_name == "Reminder"
    assert setting.is_negative_duration is True
    assert setting.default_shift_duration is None

  def test_create_with_default_duration(self):
    """Test creating alarm setting with default duration."""
    setting = AlarmSetting(
      default_shift_duration=timedelta(hours=1),
      is_negative_duration=False,
    )
    assert setting.duration_field_name == ""
    assert setting.default_shift_duration == timedelta(hours=1)

  def test_create_with_both_field_and_default(self):
    """Test creating alarm setting with both field and default."""
    setting = AlarmSetting(
      duration_field_name="Estimation",
      default_shift_duration=timedelta(minutes=30),
      is_negative_duration=True,
    )
    assert setting.duration_field_name == "Estimation"
    assert setting.default_shift_duration == timedelta(minutes=30)

  def test_validation_fails_without_duration(self):
    """Test that validation fails without any duration source."""
    with pytest.raises(ValueError, match="Either duration_field_name or default_shift_duration"):
      AlarmSetting(is_negative_duration=True)

  def test_shift_based_on_default(self):
    """Test default value for shift_based_on."""
    setting = AlarmSetting(duration_field_name="Reminder")
    assert setting.shift_based_on == ShiftBasedOn.START

  def test_shift_based_on_end(self):
    """Test setting shift_based_on to END."""
    setting = AlarmSetting(
      duration_field_name="Reminder",
      shift_based_on=ShiftBasedOn.END,
    )
    assert setting.shift_based_on == ShiftBasedOn.END


class TestStringMapping:
  """Tests for StringMapping model."""

  def test_create_with_field_name(self):
    """Test creating string mapping with field name."""
    mapping = StringMapping(
      from_field_name="Status",
      to_vevent_field=VEventField.STATUS,
    )
    assert mapping.from_field_name == "Status"
    assert mapping.default_value == ""
    assert mapping.to_vevent_field == VEventField.STATUS

  def test_create_with_default_value(self):
    """Test creating string mapping with default value."""
    mapping = StringMapping(
      default_value="BUSY",
      to_vevent_field=VEventField.TRANSP,
    )
    assert mapping.from_field_name == ""
    assert mapping.default_value == "BUSY"

  def test_create_with_both(self):
    """Test creating string mapping with both field and default."""
    mapping = StringMapping(
      from_field_name="Category",
      default_value="Work",
      to_vevent_field=VEventField.CATEGORIES,
    )
    assert mapping.from_field_name == "Category"
    assert mapping.default_value == "Work"

  def test_validation_fails_without_value(self):
    """Test that validation fails without any value source."""
    with pytest.raises(ValueError, match="Either from_field_name or default_value"):
      StringMapping(to_vevent_field=VEventField.STATUS)


class TestOtherStringMappings:
  """Tests for OtherStringMappings model."""

  def test_create_empty(self):
    """Test creating empty mappings container."""
    mappings = OtherStringMappings()
    assert mappings.mappings == []

  def test_create_with_mappings(self):
    """Test creating with multiple mappings."""
    mappings = OtherStringMappings(
      mappings=[
        StringMapping(from_field_name="Status", to_vevent_field=VEventField.STATUS),
        StringMapping(default_value="Work", to_vevent_field=VEventField.CATEGORIES),
      ]
    )
    assert len(mappings.mappings) == 2


class TestIssueMappingConfig:
  """Tests for IssueMappingConfig model."""

  def test_create_minimal(self):
    """Test creating minimal configuration."""
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("UTC"),
      ),
    )
    assert config.event_type == EventType.ONE_DAY_EVENT
    assert config.alarm_setting is None
    assert config.other_mappings.mappings == []

  def test_create_full(self):
    """Test creating full configuration."""
    config = IssueMappingConfig(
      datetime_field_info=OneDayDateTimeFieldInfo(
        field_name="Due Date",
        zone_id=ZoneInfo("UTC"),
      ),
      alarm_setting=AlarmSetting(
        duration_field_name="Reminder",
      ),
      other_mappings=OtherStringMappings(
        mappings=[
          StringMapping(from_field_name="Status", to_vevent_field=VEventField.STATUS),
        ]
      ),
    )
    assert config.alarm_setting is not None
    assert len(config.other_mappings.mappings) == 1


class TestEnums:
  """Tests for enum types."""

  def test_event_type_values(self):
    """Test EventType enum values."""
    assert EventType.ONE_DAY_EVENT.value == "one_day_event"

  def test_shift_based_on_values(self):
    """Test ShiftBasedOn enum values."""
    assert ShiftBasedOn.START.value == "start"
    assert ShiftBasedOn.END.value == "end"

  def test_vevent_field_values(self):
    """Test VEventField enum values."""
    assert VEventField.ATTENDEE.value == "attendee"
    assert VEventField.STATUS.value == "status"
    assert VEventField.TRANSP.value == "transp"
    assert VEventField.CATEGORIES.value == "categories"
