"""EventMapper - main orchestrator for mapping YouTrack issues to VEvents."""

import logging

from icalendar import Event

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical.alarm_mapper import AlarmMapper
from youcal.core.ical.datetime_setter import DateTimeFieldSetter
from youcal.core.ical.field_setter import OtherFieldsSetter
from youcal.core.ical.models import IssueMappingConfig
from youcal.core.ical.utils import get_issue_debug_name

logger = logging.getLogger(__name__)


class EventMapper:
  """Maps YouTrack issues to iCalendar VEvent components.

  This class orchestrates the mapping process using specialized setters
  for datetime, alarm, and other field mappings.

  Attributes:
    datetime_setter: Handler for DTSTART/DTEND properties
    alarm_mapper: Handler for VALARM components
    field_setter: Handler for other VEvent properties
  """

  def __init__(
    self,
    datetime_setter: DateTimeFieldSetter | None = None,
    alarm_mapper: AlarmMapper | None = None,
    field_setter: OtherFieldsSetter | None = None,
  ) -> None:
    """Initialize the EventMapper with component handlers.

    Args:
      datetime_setter: Handler for datetime fields (default: new instance)
      alarm_mapper: Handler for alarms (default: new instance)
      field_setter: Handler for other fields (default: new instance)
    """
    self._datetime_setter = datetime_setter or DateTimeFieldSetter()
    self._alarm_mapper = alarm_mapper or AlarmMapper()
    self._field_setter = field_setter or OtherFieldsSetter()

  def map_issue(
    self,
    issue: YouTrackIssue,
    config: IssueMappingConfig,
  ) -> Event:
    """Map a YouTrack issue to a VEvent component.

    Args:
      issue: The YouTrack issue to map
      config: Configuration for the mapping

    Returns:
      An icalendar Event (VEvent) component

    Raises:
      MappingException: If the issue cannot be mapped (e.g., missing date)
    """
    debug_name = get_issue_debug_name(issue)
    logger.info("Start mapping %s to VEvent", debug_name)

    # Create new VEvent
    event = Event()

    # Set datetime field (DTSTART)
    self._datetime_setter.set_datetime(event, issue, config.datetime_field_info)

    # Add common properties
    self._add_common_properties(event, issue)

    # Add alarm if configured
    alarm = self._alarm_mapper.create_alarm(issue, config.alarm_setting)
    if alarm is not None:
      event.add_component(alarm)

    # Set other field mappings
    self._field_setter.set_fields(event, issue, config.other_mappings)

    logger.info("Done mapping %s to VEvent", debug_name)
    return event

  def _add_common_properties(
    self,
    event: Event,
    issue: YouTrackIssue,
  ) -> None:
    """Add common properties to the VEvent.

    Sets UID, SUMMARY, and DESCRIPTION.

    Args:
      event: The VEvent to modify
      issue: The YouTrack issue
    """
    # UID - use readable ID for stability
    event.add("uid", issue.id_readable)

    # SUMMARY - format as "[ID] Summary"
    summary = f"[{issue.id_readable}] {issue.summary}"
    event.add("summary", summary)

    # DESCRIPTION - optional
    if issue.description:
      event.add("description", issue.description)
