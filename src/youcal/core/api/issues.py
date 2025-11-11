"""Issues API for YouTrack."""

import logging
from collections.abc import AsyncIterator
from typing import Any

from youcal.core.api.client import YouTrackClient
from youcal.core.api.models import PaginatedIssuesResponse, YouTrackIssue

logger = logging.getLogger(__name__)


class IssuesApi:
  """API client for YouTrack issues.

  This class provides methods to query and retrieve YouTrack issues
  with support for pagination and lazy loading.

  Attributes:
    client: The YouTrack HTTP client
  """

  def __init__(self, client: YouTrackClient) -> None:
    """Initialize the Issues API.

    Args:
      client: The YouTrack client instance
    """
    self._client = client

  async def get_issues(
    self,
    query: str,
    fields: list[str],
    custom_fields: list[str] | None = None,
    skip: int = 0,
    top: int = 40,
  ) -> list[YouTrackIssue]:
    """Get a single page of issues from YouTrack.

    Args:
      query: YouTrack search query (e.g., "project: MyProject")
      fields: List of fields to retrieve (e.g., ["id", "idReadable", "summary"])
      custom_fields: Optional list of custom fields with nested selection
      skip: Number of items to skip (for pagination)
      top: Maximum number of items to return

    Returns:
      List of YouTrack issues

    Raises:
      httpx.HTTPError: If the API request fails
    """
    # Build query parameters
    params: dict[str, Any] = {
      "query": query,
      "fields": ",".join(fields),
      "$skip": skip,
      "$top": top,
    }

    # Add custom fields if provided
    # Note: httpx automatically formats list values as repeated query parameters
    # e.g., ["type", "assignee"] → customFields=type&customFields=assignee
    if custom_fields:
      params["customFields"] = custom_fields

    logger.debug(
      "Fetching issues with query='%s', skip=%d, top=%d",
      query,
      skip,
      top,
    )

    # Make API request
    response = await self._client.get("/api/issues", params=params)
    response_data: list[dict[str, Any]] = response.json()

    # Parse response into Pydantic models
    paginated_response = PaginatedIssuesResponse.from_api_response(response_data)
    logger.debug("Retrieved %d issues", len(paginated_response.issues))

    return paginated_response.issues

  async def get_issues_lazy(
    self,
    query: str,
    fields: list[str],
    custom_fields: list[str] | None = None,
    start_at: int = 0,
    page_size: int = 40,
  ) -> AsyncIterator[YouTrackIssue]:
    """Get issues lazily with automatic pagination.

    This method yields issues one by one while fetching pages in the background
    as needed. It implements the same buffered pagination strategy as the Kotlin
    implementation.

    Args:
      query: YouTrack search query
      fields: List of fields to retrieve
      custom_fields: Optional list of custom fields with nested selection
      start_at: Starting offset (default: 0)
      page_size: Number of items per page (default: 40)

    Yields:
      YouTrack issues one by one

    Raises:
      httpx.HTTPError: If an API request fails
    """
    if page_size <= 0:
      raise ValueError("page_size must be greater than 0")

    skip = start_at
    still_has_more = True

    logger.debug(
      "Starting lazy issue retrieval with query='%s', start_at=%d, page_size=%d",
      query,
      start_at,
      page_size,
    )

    while still_has_more:
      # Fetch next page
      logger.debug("Reading issue list '%s' starting from %d of amount %d", query, skip, page_size)
      issues = await self.get_issues(
        query=query,
        fields=fields,
        custom_fields=custom_fields,
        skip=skip,
        top=page_size,
      )

      # Yield each issue from the page
      for issue in issues:
        yield issue

      # Update pagination state
      skip += page_size
      still_has_more = len(issues) == page_size

      if not still_has_more:
        logger.debug("No more issues to read from '%s'", query)
