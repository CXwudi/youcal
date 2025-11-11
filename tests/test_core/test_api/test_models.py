"""Tests for API models."""

import pytest
from pydantic import ValidationError

from youcal.core.api.models import (
  CustomField,
  CustomFieldValue,
  PaginatedIssuesResponse,
  Timezone,
  UserProfile,
  YouTrackIssue,
)


def test_custom_field_value_creation() -> None:
  """Test CustomFieldValue can be created with all fields."""
  value = CustomFieldValue(name="Bug", id="bug-id", minutes=120)

  assert value.name == "Bug"
  assert value.id == "bug-id"
  assert value.minutes == 120


def test_custom_field_value_optional_fields() -> None:
  """Test CustomFieldValue with optional fields as None."""
  value = CustomFieldValue()

  assert value.name is None
  assert value.id is None
  assert value.minutes is None


def test_custom_field_creation() -> None:
  """Test CustomField can be created."""
  field = CustomField(
    name="Type",
    id="type-id",
    value=CustomFieldValue(name="Bug", id="bug-id"),
  )

  assert field.name == "Type"
  assert field.id == "type-id"
  assert isinstance(field.value, CustomFieldValue)


def test_youtrack_issue_with_camel_case() -> None:
  """Test YouTrackIssue accepts camelCase field names from API."""
  issue_data = {
    "id": "issue-123",
    "idReadable": "PROJECT-42",
    "summary": "Test issue",
    "description": "Test description",
    "customFields": [
      {
        "name": "Type",
        "id": "type-id",
        "value": {"name": "Bug", "id": "bug-id"},
      }
    ],
  }

  issue = YouTrackIssue.model_validate(issue_data)

  assert issue.id == "issue-123"
  assert issue.id_readable == "PROJECT-42"
  assert issue.summary == "Test issue"
  assert issue.description == "Test description"
  assert len(issue.custom_fields) == 1
  assert issue.custom_fields[0].name == "Type"


def test_youtrack_issue_with_snake_case() -> None:
  """Test YouTrackIssue also accepts snake_case field names."""
  issue_data = {
    "id": "issue-123",
    "id_readable": "PROJECT-42",
    "summary": "Test issue",
    "custom_fields": [],
  }

  issue = YouTrackIssue.model_validate(issue_data)

  assert issue.id == "issue-123"
  assert issue.id_readable == "PROJECT-42"


def test_youtrack_issue_minimal() -> None:
  """Test YouTrackIssue with minimal required fields."""
  issue_data = {
    "id": "issue-123",
    "idReadable": "PROJECT-42",
    "summary": "Test issue",
  }

  issue = YouTrackIssue.model_validate(issue_data)

  assert issue.id == "issue-123"
  assert issue.id_readable == "PROJECT-42"
  assert issue.summary == "Test issue"
  assert issue.description is None
  assert issue.custom_fields == []


def test_youtrack_issue_validation_error() -> None:
  """Test YouTrackIssue raises validation error for missing required fields."""
  issue_data = {
    "id": "issue-123",
    # Missing idReadable and summary
  }

  with pytest.raises(ValidationError):
    YouTrackIssue.model_validate(issue_data)


def test_timezone_creation() -> None:
  """Test Timezone can be created."""
  timezone = Timezone(
    id="America/New_York",
    offset=-18000000,
    presentation="EST",
  )

  assert timezone.id == "America/New_York"
  assert timezone.offset == -18000000
  assert timezone.presentation == "EST"


def test_user_profile_creation() -> None:
  """Test UserProfile can be created."""
  profile = UserProfile(
    timezone=Timezone(id="UTC", offset=0, presentation="UTC")
  )

  assert profile.timezone.id == "UTC"


def test_paginated_issues_response_from_api() -> None:
  """Test PaginatedIssuesResponse can be created from API response."""
  api_response = [
    {
      "id": "1",
      "idReadable": "PROJECT-1",
      "summary": "First issue",
    },
    {
      "id": "2",
      "idReadable": "PROJECT-2",
      "summary": "Second issue",
    },
  ]

  response = PaginatedIssuesResponse.from_api_response(api_response)

  assert len(response.issues) == 2
  assert response.issues[0].id_readable == "PROJECT-1"
  assert response.issues[1].id_readable == "PROJECT-2"


def test_paginated_issues_response_empty() -> None:
  """Test PaginatedIssuesResponse with empty response."""
  api_response: list = []

  response = PaginatedIssuesResponse.from_api_response(api_response)

  assert len(response.issues) == 0
