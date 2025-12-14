"""Tests for EventMapper - integration tests."""

from datetime import date

import pytest

from youcal.core.api.models import YouTrackIssue
from youcal.core.ical import EventMapper, IssueMappingConfig, MappingException


class TestEventMapper:
  """Integration tests for EventMapper."""

  def test_map_issue_basic(
    self, sample_issue: YouTrackIssue, sample_config: IssueMappingConfig
  ):
    """Test mapping a basic issue to VEvent."""
    mapper = EventMapper()
    event = mapper.map_issue(sample_issue, sample_config)

    # Check UID
    assert event.get("uid") == "PROJECT-123"

    # Check SUMMARY
    assert event.get("summary") == "[PROJECT-123] Test Issue Summary"

    # Check DESCRIPTION
    assert event.get("description") == "This is a test description"

    # Check DTSTART
    dtstart = event.get("dtstart")
    assert dtstart.dt == date(2024, 1, 1)

  def test_map_issue_with_alarm(
    self, sample_issue: YouTrackIssue, sample_config: IssueMappingConfig
  ):
    """Test mapping an issue with alarm."""
    mapper = EventMapper()
    event = mapper.map_issue(sample_issue, sample_config)

    # Check that alarm component exists
    alarms = [c for c in event.subcomponents if c.name == "VALARM"]
    assert len(alarms) == 1

    alarm = alarms[0]
    assert alarm.get("action") == "DISPLAY"
    assert "Reminder for PROJECT-123" in str(alarm.get("description"))

  def test_map_issue_without_alarm(
    self, sample_issue: YouTrackIssue, config_no_alarm: IssueMappingConfig
  ):
    """Test mapping an issue without alarm setting."""
    mapper = EventMapper()
    event = mapper.map_issue(sample_issue, config_no_alarm)

    # Check that no alarm component exists
    alarms = [c for c in event.subcomponents if c.name == "VALARM"]
    assert len(alarms) == 0

  def test_map_issue_with_default_alarm(
    self, sample_issue: YouTrackIssue, config_with_default_alarm: IssueMappingConfig
  ):
    """Test mapping an issue with default alarm duration."""
    mapper = EventMapper()
    event = mapper.map_issue(sample_issue, config_with_default_alarm)

    # Check that alarm exists with default duration
    alarms = [c for c in event.subcomponents if c.name == "VALARM"]
    assert len(alarms) == 1

  def test_map_issue_with_categories(
    self, sample_issue: YouTrackIssue, sample_config: IssueMappingConfig
  ):
    """Test mapping an issue with categories field."""
    mapper = EventMapper()
    event = mapper.map_issue(sample_issue, sample_config)

    # Check CATEGORIES
    categories = event.get("categories")
    assert categories is not None
    # Categories should contain "High" from Priority field
    assert "High" in categories.cats

  def test_map_issue_missing_date_raises(
    self, sample_issue_no_date: YouTrackIssue, sample_config: IssueMappingConfig
  ):
    """Test that mapping an issue without date raises MappingException."""
    mapper = EventMapper()

    with pytest.raises(MappingException) as exc_info:
      mapper.map_issue(sample_issue_no_date, sample_config)

    assert "PROJECT-456" in str(exc_info.value)
    assert "Due Date" in str(exc_info.value)

  def test_map_issue_no_description(self, sample_config: IssueMappingConfig):
    """Test mapping an issue without description."""
    issue = YouTrackIssue(
      id="1",
      idReadable="TEST-1",
      summary="No Description Issue",
      description=None,
      customFields=[
        {
          "name": "Due Date",
          "id": "f1",
          "value": 1704067200000,
        },
      ],
    )

    mapper = EventMapper()
    event = mapper.map_issue(issue, sample_config)

    # DESCRIPTION should not be set
    assert event.get("description") is None
