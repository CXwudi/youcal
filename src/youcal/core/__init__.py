"""Core module for YouCal - designed to be serverless-ready.

This module contains the core functionality for YouCal:
- YouTrack API client (youcal.core.api)
- iCalendar generation (youcal.core.ical) - Coming in Phase 2
- Configuration models (youcal.core.config) - Coming in Phase 2

Example usage:
  ```python
  from youcal.core.api import YouTrackAuth, YouTrackClient, IssuesApi

  auth = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="perm:xxx"
  )

  async with YouTrackClient(auth) as client:
    issues_api = IssuesApi(client)
    async for issue in issues_api.get_issues_lazy(
      query="project: MyProject",
      fields=["id", "idReadable", "summary"],
      page_size=40
    ):
      print(f"{issue.id_readable}: {issue.summary}")
  ```
"""

from youcal.core.api import (
  IssuesApi,
  UsersApi,
  YouTrackAuth,
  YouTrackClient,
  YouTrackIssue,
)

__all__ = [
  "YouTrackAuth",
  "YouTrackClient",
  "IssuesApi",
  "UsersApi",
  "YouTrackIssue",
]
