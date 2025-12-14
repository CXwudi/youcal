"""Consolidated utilities for iCalendar mapping."""

import logging
from datetime import timedelta

from youcal.core.api.models import CustomField, CustomFieldValue, YouTrackIssue

logger = logging.getLogger(__name__)


# =============================================================================
# YouTrack Field Types (from YouTrackType.kt)
# =============================================================================


class YouTrackFieldType:
  """Constants for YouTrack custom field $type values."""

  DATE_ISSUE_CUSTOM_FIELD = "DateIssueCustomField"
  PERIOD_ISSUE_CUSTOM_FIELD = "PeriodIssueCustomField"
  SINGLE_ENUM_ISSUE_CUSTOM_FIELD = "SingleEnumIssueCustomField"
  MULTI_ENUM_ISSUE_CUSTOM_FIELD = "MultiEnumIssueCustomField"
  STATE_ISSUE_CUSTOM_FIELD = "StateIssueCustomField"


# =============================================================================
# YouTrack Default DateTime Fields (from YouTrackDefaultDateTime.kt)
# =============================================================================


class YouTrackDefaultDateTimeField:
  """Constants for YouTrack's built-in datetime fields."""

  CREATED = "created"
  UPDATED = "updated"
  RESOLVED = "resolved"

  @classmethod
  def is_default_field(cls, field_name: str) -> bool:
    """Check if a field name is a built-in YouTrack datetime field.

    Args:
      field_name: The field name to check

    Returns:
      True if this is a built-in field (created, updated, resolved)
    """
    return field_name in (cls.CREATED, cls.UPDATED, cls.RESOLVED)


# =============================================================================
# Issue Field Extraction Helpers (from YouTrackIssueJsonUtil.kt)
# =============================================================================


def get_issue_debug_name(issue: YouTrackIssue) -> str:
  """Get a debug-friendly name for a YouTrack issue.

  Args:
    issue: The YouTrack issue

  Returns:
    A string like "Issue PROJECT-123 My Issue Summary"
  """
  return f"Issue {issue.id_readable} {issue.summary}"


def find_custom_field(
  issue: YouTrackIssue,
  field_name: str,
) -> CustomField | None:
  """Find a custom field by name in a YouTrack issue.

  Args:
    issue: The YouTrack issue to search
    field_name: The name of the custom field

  Returns:
    The custom field if found, None otherwise
  """
  for field in issue.custom_fields:
    if field.name == field_name:
      return field
  return None


def extract_field_date_value(
  issue: YouTrackIssue,
  field_name: str,
) -> int | None:
  """Extract a date value (epoch milliseconds) from a YouTrack issue field.

  Handles both default fields (created, updated, resolved) and custom date fields.

  Args:
    issue: The YouTrack issue
    field_name: The field name to extract from

  Returns:
    The date value as epoch milliseconds, or None if not available
  """
  if YouTrackDefaultDateTimeField.is_default_field(field_name):
    logger.debug("Field '%s' is a default datetime field", field_name)
    # Default fields would be direct attributes - not yet supported
    return None

  field = find_custom_field(issue, field_name)
  if field is None:
    return None

  value = field.value
  if value is None:
    return None

  if isinstance(value, int):
    return value

  if isinstance(value, dict):
    return value.get("value")

  return None


def extract_field_string_value(
  issue: YouTrackIssue,
  field_name: str,
) -> str | None:
  """Extract a string value from a YouTrack issue field.

  Handles single values and arrays (joined with commas).

  Args:
    issue: The YouTrack issue
    field_name: The field name to extract from

  Returns:
    The string value, or None if not available
  """
  field = find_custom_field(issue, field_name)
  if field is None:
    return None

  value = field.value
  if value is None:
    return None

  # Handle CustomFieldValue (single enum value)
  if isinstance(value, CustomFieldValue):
    return value.name

  # Handle list of values (multi-enum)
  if isinstance(value, list):
    names = []
    for item in value:
      if isinstance(item, dict) and "name" in item:
        name = item.get("name")
        if name and name != "null":
          names.append(str(name))
      elif isinstance(item, CustomFieldValue) and item.name:
        names.append(item.name)
    return ",".join(names) if names else None

  # Handle dict with name
  if isinstance(value, dict) and "name" in value:
    return value.get("name")

  # Handle string directly
  if isinstance(value, str):
    return value

  return None


def extract_period_minutes(
  issue: YouTrackIssue,
  field_name: str,
) -> int | None:
  """Extract period/duration value in minutes from a YouTrack issue field.

  Args:
    issue: The YouTrack issue
    field_name: The period field name

  Returns:
    The duration in minutes, or None if not available
  """
  field = find_custom_field(issue, field_name)
  if field is None:
    return None

  value = field.value
  if value is None:
    return None

  # Handle CustomFieldValue with minutes
  if isinstance(value, CustomFieldValue):
    return value.minutes

  # Handle dict with minutes
  if isinstance(value, dict) and "minutes" in value:
    return value.get("minutes")

  return None


# =============================================================================
# Duration Conversion (from AlarmMapper.kt)
# =============================================================================


def convert_youtrack_minutes_to_timedelta(minutes: int) -> timedelta:
  """Convert YouTrack duration minutes to a Python timedelta.

  YouTrack represents durations with:
  - 1 week = 5 working days (excludes weekends)
  - 1 day = 8 working hours

  This function converts to real calendar time:
  - Weeks become 7 real days
  - Days become real days

  Args:
    minutes: Duration in YouTrack minutes

  Returns:
    A timedelta representing the real calendar duration

  Example:
    >>> convert_youtrack_minutes_to_timedelta(60)  # 1 hour
    timedelta(hours=1)
    >>> convert_youtrack_minutes_to_timedelta(480)  # 1 YT day = 8 hours
    timedelta(days=1)
    >>> convert_youtrack_minutes_to_timedelta(2400)  # 1 YT week = 5*8 hours
    timedelta(days=7)
  """
  hours = minutes // 60
  remaining_minutes = minutes % 60

  # YouTrack: 1 week = 5 days * 8 hours = 40 hours
  yt_hours_per_week = 8 * 5  # 40 hours
  weeks = hours // yt_hours_per_week
  remaining_hours_after_weeks = hours % yt_hours_per_week

  # YouTrack: 1 day = 8 hours
  yt_hours_per_day = 8
  days = remaining_hours_after_weeks // yt_hours_per_day
  remaining_hours = remaining_hours_after_weeks % yt_hours_per_day

  # Convert to real calendar time: weeks -> 7 days
  total_days = weeks * 7 + days

  return timedelta(days=total_days, hours=remaining_hours, minutes=remaining_minutes)
