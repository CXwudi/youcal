"""CalendarBuilder for assembling VEvents into a Calendar."""

import logging
from zoneinfo import ZoneInfo

from icalendar import Calendar, Event

logger = logging.getLogger(__name__)


class CalendarBuilder:
  """Builds iCalendar objects from VEvent components.

  This class assembles multiple VEvents into a complete iCalendar,
  adding required properties and optional timezone components.

  Attributes:
    prod_id: Product identifier for the PRODID property
  """

  def __init__(self, prod_id: str = "-//YouCal//YouTrack to iCal//EN") -> None:
    """Initialize the CalendarBuilder.

    Args:
      prod_id: Product identifier for the calendar
    """
    self._prod_id = prod_id

  def build(
    self,
    events: list[Event],
    timezone: ZoneInfo | None = None,
  ) -> Calendar:
    """Build a Calendar from a list of events.

    Args:
      events: List of VEvent components to include
      timezone: Optional timezone for reference (not yet fully implemented)

    Returns:
      A complete Calendar object
    """
    logger.info("Building calendar with %d events", len(events))

    cal = Calendar()

    # Add required properties
    cal.add("prodid", self._prod_id)
    cal.add("version", "2.0")

    # Add timezone component if specified
    if timezone is not None:
      self._add_timezone(cal, timezone)

    # Add all events
    for event in events:
      cal.add_component(event)

    logger.info("Calendar built successfully")
    return cal

  def _add_timezone(self, cal: Calendar, zone_info: ZoneInfo) -> None:
    """Add a VTIMEZONE component to the calendar.

    Note: Full VTIMEZONE generation requires additional timezone data.
    For now, we just log that a timezone was specified.

    Args:
      cal: The calendar to modify
      zone_info: The timezone to add
    """
    # The icalendar library handles timezone components
    # For now, we just set TZID on properties that need it
    # A full VTIMEZONE implementation would require pytz or tzdata
    logger.debug("Timezone '%s' specified for calendar", zone_info.key)

  def to_ical_string(self, calendar: Calendar) -> str:
    """Convert a Calendar to iCalendar format string.

    Args:
      calendar: The Calendar to convert

    Returns:
      The iCalendar format string (UTF-8)
    """
    return calendar.to_ical().decode("utf-8")

  def to_ical_bytes(self, calendar: Calendar) -> bytes:
    """Convert a Calendar to iCalendar format bytes.

    Args:
      calendar: The Calendar to convert

    Returns:
      The iCalendar format bytes
    """
    return calendar.to_ical()
