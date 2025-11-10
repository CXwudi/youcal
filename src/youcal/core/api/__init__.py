"""YouTrack API client module.

This module provides async API clients for interacting with YouTrack.

Example usage:
  ```python
  from youcal.core.api import YouTrackAuth, YouTrackClient, IssuesApi, UsersApi

  auth = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="perm:xxx"
  )

  async with YouTrackClient(auth) as client:
    issues_api = IssuesApi(client)
    users_api = UsersApi(client)

    # Fetch issues lazily
    async for issue in issues_api.get_issues_lazy(
      query="project: MyProject",
      fields=["id", "idReadable", "summary"],
      page_size=40
    ):
      print(f"{issue.id_readable}: {issue.summary}")

    # Get user timezone
    timezone = await users_api.get_timezone()
  ```
"""

from youcal.core.api.auth import YouTrackAuth
from youcal.core.api.client import YouTrackClient
from youcal.core.api.issues import IssuesApi
from youcal.core.api.models import (
  CustomField,
  CustomFieldValue,
  PaginatedIssuesResponse,
  Timezone,
  UserProfile,
  YouTrackIssue,
)
from youcal.core.api.users import UsersApi

__all__ = [
  # Authentication
  "YouTrackAuth",
  # Client
  "YouTrackClient",
  # API classes
  "IssuesApi",
  "UsersApi",
  # Models
  "YouTrackIssue",
  "CustomField",
  "CustomFieldValue",
  "UserProfile",
  "Timezone",
  "PaginatedIssuesResponse",
]
