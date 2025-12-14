"""OtherFieldsSetter for mapping custom fields to VEvent properties."""

import logging

from icalendar import Event

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical.models import OtherStringMappings, StringMapping, VEventField
from youcal.core.ical.utils import extract_field_string_value, get_issue_debug_name

logger = logging.getLogger(__name__)


class OtherFieldsSetter:
  """Maps YouTrack custom fields to VEvent string properties.

  This class handles extracting values from YouTrack issues and setting
  corresponding VEvent properties like CATEGORIES, STATUS, TRANSP, ATTENDEE.
  """

  def set_fields(
    self,
    event: Event,
    issue: YouTrackIssue,
    mappings: OtherStringMappings,
  ) -> None:
    """Set additional VEvent properties from YouTrack fields.

    Args:
      event: The icalendar VEvent component
      issue: The YouTrack issue to extract values from
      mappings: Configuration for field mappings
    """
    debug_name = get_issue_debug_name(issue)
    logger.info("Mapping string fields for %s", debug_name)

    for mapping in mappings.mappings:
      self._set_single_field(event, issue, mapping)

  def _set_single_field(
    self,
    event: Event,
    issue: YouTrackIssue,
    mapping: StringMapping,
  ) -> None:
    """Set a single VEvent property from a mapping.

    Args:
      event: The icalendar VEvent component
      issue: The YouTrack issue
      mapping: The field mapping configuration
    """
    debug_name = get_issue_debug_name(issue)
    target_field = mapping.to_vevent_field

    # Try to get value from field
    value: str | None = None
    if mapping.from_field_name:
      value = extract_field_string_value(issue, mapping.from_field_name)

    # Fall back to default if no value
    if not value and mapping.default_value:
      value = mapping.default_value
      logger.debug(
        "Using default value '%s' for %s on %s",
        value,
        target_field.value,
        debug_name,
      )
    elif value:
      logger.debug(
        "Mapped field '%s' value '%s' to %s for %s",
        mapping.from_field_name,
        value,
        target_field.value,
        debug_name,
      )

    if not value:
      logger.warning(
        "No value available for %s mapping on %s",
        target_field.value,
        debug_name,
      )
      return

    # Add the property to the event
    self._add_property(event, target_field, value)

  def _add_property(
    self,
    event: Event,
    field: VEventField,
    value: str,
  ) -> None:
    """Add a property to the VEvent.

    Args:
      event: The icalendar VEvent component
      field: The target VEvent field type
      value: The value to set
    """
    if field == VEventField.ATTENDEE:
      # ATTENDEE expects a URI format
      event.add("attendee", f"mailto:{value}" if "@" in value else value)
    elif field == VEventField.STATUS:
      # STATUS values should be uppercase
      event.add("status", value.upper())
    elif field == VEventField.TRANSP:
      # TRANSP values should be uppercase (OPAQUE or TRANSPARENT)
      event.add("transp", value.upper())
    elif field == VEventField.CATEGORIES:
      # CATEGORIES can be comma-separated
      categories = [c.strip() for c in value.split(",")]
      event.add("categories", categories)
