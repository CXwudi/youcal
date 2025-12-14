"""Tests for iCalendar utility functions."""

from datetime import timedelta

from youcal.core.api.models import CustomField, YouTrackIssue
from youcal.core.ical.utils import (
  YouTrackDefaultDateTimeField,
  YouTrackFieldType,
  convert_youtrack_minutes_to_timedelta,
  extract_field_date_value,
  extract_field_string_value,
  extract_period_minutes,
  find_custom_field,
  get_issue_debug_name,
)


class TestYouTrackFieldType:
  """Tests for YouTrackFieldType constants."""

  def test_field_type_constants(self):
    """Test that field type constants are defined."""
    assert YouTrackFieldType.DATE_ISSUE_CUSTOM_FIELD == "DateIssueCustomField"
    assert YouTrackFieldType.PERIOD_ISSUE_CUSTOM_FIELD == "PeriodIssueCustomField"
    assert YouTrackFieldType.STATE_ISSUE_CUSTOM_FIELD == "StateIssueCustomField"


class TestYouTrackDefaultDateTimeField:
  """Tests for YouTrackDefaultDateTimeField."""

  def test_default_field_constants(self):
    """Test that default field constants are defined."""
    assert YouTrackDefaultDateTimeField.CREATED == "created"
    assert YouTrackDefaultDateTimeField.UPDATED == "updated"
    assert YouTrackDefaultDateTimeField.RESOLVED == "resolved"

  def test_is_default_field_true(self):
    """Test is_default_field returns True for default fields."""
    assert YouTrackDefaultDateTimeField.is_default_field("created") is True
    assert YouTrackDefaultDateTimeField.is_default_field("updated") is True
    assert YouTrackDefaultDateTimeField.is_default_field("resolved") is True

  def test_is_default_field_false(self):
    """Test is_default_field returns False for custom fields."""
    assert YouTrackDefaultDateTimeField.is_default_field("Due Date") is False
    assert YouTrackDefaultDateTimeField.is_default_field("Start Date") is False


class TestGetIssueDebugName:
  """Tests for get_issue_debug_name function."""

  def test_debug_name_format(self, sample_issue: YouTrackIssue):
    """Test debug name format."""
    debug_name = get_issue_debug_name(sample_issue)
    assert debug_name == "Issue PROJECT-123 Test Issue Summary"


class TestFindCustomField:
  """Tests for find_custom_field function."""

  def test_find_existing_field(self, sample_issue: YouTrackIssue):
    """Test finding an existing custom field."""
    field = find_custom_field(sample_issue, "Due Date")
    assert field is not None
    assert field.name == "Due Date"

  def test_find_nonexistent_field(self, sample_issue: YouTrackIssue):
    """Test finding a non-existent custom field."""
    field = find_custom_field(sample_issue, "Nonexistent")
    assert field is None


class TestExtractFieldDateValue:
  """Tests for extract_field_date_value function."""

  def test_extract_date_value(self, sample_issue: YouTrackIssue):
    """Test extracting date value from custom field."""
    value = extract_field_date_value(sample_issue, "Due Date")
    assert value == 1704067200000

  def test_extract_nonexistent_field(self, sample_issue: YouTrackIssue):
    """Test extracting from non-existent field."""
    value = extract_field_date_value(sample_issue, "Nonexistent")
    assert value is None

  def test_extract_default_field_not_supported(self, sample_issue: YouTrackIssue):
    """Test that default fields return None (not yet supported)."""
    value = extract_field_date_value(sample_issue, "created")
    assert value is None

  def test_extract_date_as_direct_int(self):
    """Test extracting date directly as int (YouTrack format)."""
    issue = YouTrackIssue(
      id="1",
      idReadable="TEST-1",
      summary="Test",
      customFields=[
        CustomField(name="Date", id="f1", value=1704067200000),
      ],
    )
    value = extract_field_date_value(issue, "Date")
    assert value == 1704067200000


class TestExtractFieldStringValue:
  """Tests for extract_field_string_value function."""

  def test_extract_from_custom_field_value(self, sample_issue: YouTrackIssue):
    """Test extracting string from CustomFieldValue."""
    value = extract_field_string_value(sample_issue, "Priority")
    assert value == "High"

  def test_extract_from_dict_with_name(self, sample_issue: YouTrackIssue):
    """Test extracting string from dict with name key."""
    value = extract_field_string_value(sample_issue, "State")
    assert value == "Open"

  def test_extract_from_array(self, sample_issue: YouTrackIssue):
    """Test extracting string from array (multi-value field)."""
    value = extract_field_string_value(sample_issue, "Tags")
    assert value == "Bug,Critical"

  def test_extract_nonexistent_field(self, sample_issue: YouTrackIssue):
    """Test extracting from non-existent field."""
    value = extract_field_string_value(sample_issue, "Nonexistent")
    assert value is None

  def test_extract_direct_string(self):
    """Test extracting direct string value."""
    issue = YouTrackIssue(
      id="1",
      idReadable="TEST-1",
      summary="Test",
      customFields=[
        CustomField(name="Text", id="f1", value="Direct string"),
      ],
    )
    value = extract_field_string_value(issue, "Text")
    assert value == "Direct string"


class TestExtractPeriodMinutes:
  """Tests for extract_period_minutes function."""

  def test_extract_from_custom_field_value(self, sample_issue: YouTrackIssue):
    """Test extracting minutes from CustomFieldValue."""
    minutes = extract_period_minutes(sample_issue, "Reminder")
    assert minutes == 480

  def test_extract_from_dict_with_minutes(self):
    """Test extracting minutes from dict with minutes key."""
    issue = YouTrackIssue(
      id="1",
      idReadable="TEST-1",
      summary="Test",
      customFields=[
        CustomField(name="Duration", id="f1", value={"minutes": 120}),
      ],
    )
    minutes = extract_period_minutes(issue, "Duration")
    assert minutes == 120

  def test_extract_nonexistent_field(self, sample_issue: YouTrackIssue):
    """Test extracting from non-existent field."""
    minutes = extract_period_minutes(sample_issue, "Nonexistent")
    assert minutes is None


class TestConvertYoutrackMinutesToTimedelta:
  """Tests for convert_youtrack_minutes_to_timedelta function."""

  def test_convert_minutes_only(self):
    """Test converting minutes only."""
    result = convert_youtrack_minutes_to_timedelta(30)
    assert result == timedelta(minutes=30)

  def test_convert_one_hour(self):
    """Test converting one hour."""
    result = convert_youtrack_minutes_to_timedelta(60)
    assert result == timedelta(hours=1)

  def test_convert_one_yt_day(self):
    """Test converting one YouTrack day (8 hours)."""
    result = convert_youtrack_minutes_to_timedelta(480)
    assert result == timedelta(days=1)

  def test_convert_one_yt_week(self):
    """Test converting one YouTrack week (5 * 8 = 40 hours)."""
    result = convert_youtrack_minutes_to_timedelta(2400)
    assert result == timedelta(days=7)

  def test_convert_complex_duration(self):
    """Test converting complex duration."""
    # 1 week (40h) + 2 days (16h) + 3 hours + 45 minutes
    # = 2400 + 960 + 180 + 45 = 3585 minutes
    result = convert_youtrack_minutes_to_timedelta(3585)
    # Should be 7 days + 2 days + 3 hours + 45 minutes
    assert result == timedelta(days=9, hours=3, minutes=45)

  def test_convert_zero(self):
    """Test converting zero minutes."""
    result = convert_youtrack_minutes_to_timedelta(0)
    assert result == timedelta()
