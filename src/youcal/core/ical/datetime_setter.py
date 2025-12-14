"""DateTimeFieldSetter for mapping date fields to VEvent DTSTART."""

import logging
from datetime import datetime

from icalendar import Event

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical.exceptions import MappingException
from youcal.core.ical.models import OneDayDateTimeFieldInfo
from youcal.core.ical.utils import (
  YouTrackDefaultDateTimeField,
  find_custom_field,
  get_issue_debug_name,
)

logger = logging.getLogger(__name__)


class DateTimeFieldSetter:
  """Sets date/time properties on VEvents based on YouTrack issue fields.

  This class handles extracting date values from YouTrack issues and
  converting them to iCalendar DTSTART/DTEND properties.
  """

  def set_datetime(
    self,
    event: Event,
    issue: YouTrackIssue,
    datetime_info: OneDayDateTimeFieldInfo,
  ) -> None:
    """Set the DTSTART property on a VEvent for a one-day event.

    Args:
      event: The icalendar VEvent component
      issue: The YouTrack issue to extract date from
      datetime_info: Configuration specifying which field to use

    Raises:
      MappingException: If the date field is missing or empty
    """
    debug_name = get_issue_debug_name(issue)
    logger.info("Mapping to one-day VEvent for %s", debug_name)

    field_name = datetime_info.field_name
    date_value_ms = self._extract_date_value(issue, field_name)

    if date_value_ms is None:
      logger.warning(
        "Cannot map %s to VEvent due to missing proper start date",
        debug_name,
      )
      raise MappingException(
        f"Start date field '{field_name}' is null or missing, skipping this issue",
        issue_id=issue.id_readable,
      )

    # Convert epoch milliseconds to local date in the configured timezone
    dt = datetime.fromtimestamp(date_value_ms / 1000, tz=datetime_info.zone_id)
    local_date = dt.date()

    # Set DTSTART as DATE (all-day event)
    event.add("dtstart", local_date)
    logger.debug("Set DTSTART to %s for %s", local_date, debug_name)

  def _extract_date_value(
    self,
    issue: YouTrackIssue,
    field_name: str,
  ) -> int | None:
    """Extract date value (epoch milliseconds) from issue.

    Args:
      issue: The YouTrack issue
      field_name: Name of the field containing the date

    Returns:
      Epoch milliseconds or None if not found
    """
    # Check if it's a default field (created, updated, resolved)
    if YouTrackDefaultDateTimeField.is_default_field(field_name):
      logger.debug(
        "Field '%s' is a default datetime field - not yet supported",
        field_name,
      )
      return None

    # Look in custom fields
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
