"""Pydantic models for YouTrack API responses."""

from typing import Any

from pydantic import BaseModel, ConfigDict, Field


class CustomFieldValue(BaseModel):
  """Represents a custom field value in YouTrack.

  Attributes:
    name: The display name of the field value
    id: The unique identifier of the field value
    minutes: Optional duration value in minutes (for period fields)
  """

  name: str | None = None
  id: str | None = None
  minutes: int | None = None


class CustomField(BaseModel):
  """Represents a custom field in a YouTrack issue.

  Attributes:
    name: The name of the custom field
    id: The unique identifier of the custom field
    value: The value of the custom field (can be a single value or nested object)
  """

  name: str
  id: str
  value: CustomFieldValue | dict[str, Any] | list[Any] | str | int | None = None


class YouTrackIssue(BaseModel):
  """Represents a YouTrack issue from the API.

  Attributes:
    id: The internal ID of the issue
    id_readable: The human-readable ID (e.g., "PROJECT-123")
    summary: The issue summary/title
    description: The issue description (optional)
    custom_fields: List of custom fields attached to the issue
  """

  id: str
  id_readable: str = Field(alias="idReadable")
  summary: str
  description: str | None = None
  custom_fields: list[CustomField] = Field(default_factory=list, alias="customFields")

  model_config = ConfigDict(
    populate_by_name=True,  # Allow both snake_case and camelCase field names
  )


class Timezone(BaseModel):
  """Represents timezone information from YouTrack user profile.

  Attributes:
    id: The timezone ID (e.g., "America/New_York", "UTC")
    offset: The UTC offset in milliseconds
    presentation: Human-readable timezone presentation
  """

  id: str
  offset: int | None = None
  presentation: str | None = None


class UserProfile(BaseModel):
  """Represents a YouTrack user's general profile.

  Attributes:
    timezone: The user's timezone information
  """

  timezone: Timezone


class PaginatedIssuesResponse(BaseModel):
  """Wrapper for paginated issues response.

  This model represents the raw array response from YouTrack's issues API.
  The API returns a JSON array of issues directly.

  Attributes:
    issues: List of issues in the current page
  """

  issues: list[YouTrackIssue]

  @classmethod
  def from_api_response(cls, response_data: list[dict[str, Any]]) -> "PaginatedIssuesResponse":
    """Create a PaginatedIssuesResponse from raw API response data.

    Args:
      response_data: The raw JSON array from the API

    Returns:
      A PaginatedIssuesResponse instance
    """
    issues = [YouTrackIssue.model_validate(issue_data) for issue_data in response_data]
    return cls(issues=issues)
