"""Shared test fixtures for iCalendar tests."""

from datetime import timedelta
from zoneinfo import ZoneInfo

import pytest

from youcal.core.api.models import CustomField, CustomFieldValue, YouTrackIssue
from youcal.core.ical.models import (
  AlarmSetting,
  IssueMappingConfig,
  OneDayDateTimeFieldInfo,
  OtherStringMappings,
  ShiftBasedOn,
  StringMapping,
  VEventField,
)


@pytest.fixture
def sample_issue() -> YouTrackIssue:
  """Create a sample YouTrack issue for testing."""
  return YouTrackIssue(
    id="2-123",
    idReadable="PROJECT-123",
    summary="Test Issue Summary",
    description="This is a test description",
    customFields=[
      CustomField(
        name="Due Date",
        id="field-1",
        value=1704067200000,  # 2024-01-01 00:00:00 UTC
      ),
      CustomField(
        name="Priority",
        id="field-2",
        value=CustomFieldValue(name="High", id="prio-1"),
      ),
      CustomField(
        name="Reminder",
        id="field-3",
        value=CustomFieldValue(name=None, id=None, minutes=480),  # 1 YT day = 8 hours
      ),
      CustomField(
        name="State",
        id="field-4",
        value={"name": "Open", "id": "state-1"},
      ),
      CustomField(
        name="Tags",
        id="field-5",
        value=[{"name": "Bug", "id": "tag-1"}, {"name": "Critical", "id": "tag-2"}],
      ),
    ],
  )


@pytest.fixture
def sample_issue_no_date() -> YouTrackIssue:
  """Create a sample YouTrack issue without a date field."""
  return YouTrackIssue(
    id="2-456",
    idReadable="PROJECT-456",
    summary="Issue Without Date",
    description=None,
    customFields=[
      CustomField(
        name="Priority",
        id="field-2",
        value=CustomFieldValue(name="Low", id="prio-2"),
      ),
    ],
  )


@pytest.fixture
def sample_config() -> IssueMappingConfig:
  """Create a sample mapping configuration."""
  return IssueMappingConfig(
    datetime_field_info=OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("UTC"),
    ),
    alarm_setting=AlarmSetting(
      duration_field_name="Reminder",
      is_negative_duration=True,
      default_shift_duration=None,
      shift_based_on=ShiftBasedOn.START,
    ),
    other_mappings=OtherStringMappings(
      mappings=[
        StringMapping(
          from_field_name="Priority",
          default_value="",
          to_vevent_field=VEventField.CATEGORIES,
        ),
      ]
    ),
  )


@pytest.fixture
def config_with_default_alarm() -> IssueMappingConfig:
  """Create a configuration with default alarm duration."""
  return IssueMappingConfig(
    datetime_field_info=OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("UTC"),
    ),
    alarm_setting=AlarmSetting(
      duration_field_name="",
      is_negative_duration=True,
      default_shift_duration=timedelta(hours=1),
      shift_based_on=ShiftBasedOn.START,
    ),
  )


@pytest.fixture
def config_no_alarm() -> IssueMappingConfig:
  """Create a configuration without alarm."""
  return IssueMappingConfig(
    datetime_field_info=OneDayDateTimeFieldInfo(
      field_name="Due Date",
      zone_id=ZoneInfo("UTC"),
    ),
    alarm_setting=None,
  )
