"""AlarmMapper for creating VAlarm components from YouTrack duration fields."""

import logging
from datetime import timedelta

from icalendar import Alarm

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical.models import AlarmSetting, ShiftBasedOn
from youcal.core.ical.utils import (
  convert_youtrack_minutes_to_timedelta,
  extract_period_minutes,
  get_issue_debug_name,
)

logger = logging.getLogger(__name__)


class AlarmMapper:
  """Creates VAlarm components from YouTrack duration fields.

  This class handles extracting duration values from YouTrack issues
  and converting them to iCalendar VALARM components.
  """

  def create_alarm(
    self,
    issue: YouTrackIssue,
    alarm_setting: AlarmSetting | None,
  ) -> Alarm | None:
    """Create a VAlarm component from alarm settings.

    Args:
      issue: The YouTrack issue
      alarm_setting: Configuration for the alarm, or None to disable

    Returns:
      A VAlarm component, or None if alarm should be skipped
    """
    debug_name = get_issue_debug_name(issue)

    if alarm_setting is None:
      logger.info("No alarm setting for %s, skipping alarm creation", debug_name)
      return None

    logger.info("Creating VAlarm based on alarm setting for %s", debug_name)

    # Try to get duration and description
    result = self._get_duration_and_description(issue, alarm_setting)
    if result is None:
      return None

    duration, description = result

    # Create the alarm
    alarm = Alarm()
    alarm.add("action", "DISPLAY")
    alarm.add("description", description)

    # Set trigger - negative for before event, positive for after
    # The trigger is relative to DTSTART by default, or DTEND if specified
    if alarm_setting.shift_based_on == ShiftBasedOn.END:
      alarm.add("trigger", duration, parameters={"RELATED": "END"})
    else:
      alarm.add("trigger", duration)

    return alarm

  def _get_duration_and_description(
    self,
    issue: YouTrackIssue,
    alarm_setting: AlarmSetting,
  ) -> tuple[timedelta, str] | None:
    """Get the alarm duration and description text.

    Args:
      issue: The YouTrack issue
      alarm_setting: The alarm configuration

    Returns:
      Tuple of (duration, description) or None if alarm should be skipped
    """
    debug_name = get_issue_debug_name(issue)
    description_prefix = f"Reminder for {issue.id_readable}"

    # Try to get duration from field
    if alarm_setting.duration_field_name:
      duration = self._get_duration_from_field(issue, alarm_setting)
      if duration is not None:
        logger.info(
          "Creating VAlarm using duration from field '%s' for %s",
          alarm_setting.duration_field_name,
          debug_name,
        )
        direction = "before" if alarm_setting.is_negative_duration else "after"
        description = f"{description_prefix} with duration {duration} {direction}"
        return (duration, description)

      # Field specified but no value - fall back to default
      if alarm_setting.default_shift_duration is not None:
        logger.info(
          "Creating VAlarm using fallback default duration for %s", debug_name
        )
        duration = self._apply_sign(
          alarm_setting.default_shift_duration, alarm_setting.is_negative_duration
        )
        description = (
          f"{description_prefix} (fallback to default "
          f"{alarm_setting.default_shift_duration})"
        )
        return (duration, description)

      # No field value and no default
      logger.info(
        "No default duration set while missing duration value from field '%s' for %s, "
        "skipping alarm",
        alarm_setting.duration_field_name,
        debug_name,
      )
      return None

    # No field specified - use default if available
    if alarm_setting.default_shift_duration is not None:
      logger.info("Creating VAlarm using default duration for %s", debug_name)
      duration = self._apply_sign(
        alarm_setting.default_shift_duration, alarm_setting.is_negative_duration
      )
      description = f"{description_prefix} (default {alarm_setting.default_shift_duration})"
      return (duration, description)

    logger.warning("No alarm duration available for %s", debug_name)
    return None

  def _get_duration_from_field(
    self,
    issue: YouTrackIssue,
    alarm_setting: AlarmSetting,
  ) -> timedelta | None:
    """Extract and convert duration from a YouTrack period field.

    Args:
      issue: The YouTrack issue
      alarm_setting: The alarm configuration

    Returns:
      The converted timedelta, or None if field is missing/empty
    """
    debug_name = get_issue_debug_name(issue)
    field_name = alarm_setting.duration_field_name

    minutes = extract_period_minutes(issue, field_name)
    if minutes is None:
      logger.debug(
        "%s doesn't have value in field '%s' for creating alarm",
        debug_name,
        field_name,
      )
      return None

    # Convert YouTrack work minutes to real calendar duration
    duration = convert_youtrack_minutes_to_timedelta(minutes)

    # Apply sign based on configuration
    return self._apply_sign(duration, alarm_setting.is_negative_duration)

  def _apply_sign(self, duration: timedelta, is_negative: bool) -> timedelta:
    """Apply sign to duration based on configuration.

    Args:
      duration: The duration value
      is_negative: If True, return negative duration

    Returns:
      The duration with appropriate sign
    """
    if is_negative:
      return -duration
    return duration
