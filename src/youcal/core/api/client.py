"""YouTrack API client."""

import logging
from typing import Any, Self
from types import TracebackType

import httpx

from youcal.core.api.auth import YouTrackAuth

logger = logging.getLogger(__name__)


class YouTrackClient:
  """Async HTTP client for YouTrack API.

  This client manages the HTTP connection to YouTrack and handles
  authentication. It should be used as an async context manager.

  Example:
    ```python
    auth = YouTrackAuth(
      base_url="https://youtrack.example.com",
      bearer_token="perm:xxx"
    )
    async with YouTrackClient(auth) as client:
      issues_api = IssuesApi(client)
      issues = await issues_api.get_issues(query="project: MyProject")
    ```

  Attributes:
    auth: The authentication configuration
  """

  def __init__(self, auth: YouTrackAuth, timeout: float = 30.0) -> None:
    """Initialize the YouTrack client.

    Args:
      auth: Authentication configuration
      timeout: HTTP request timeout in seconds (default: 30.0)
    """
    self.auth = auth
    self._timeout = timeout
    self._client: httpx.AsyncClient | None = None

  async def __aenter__(self) -> Self:
    """Enter the async context manager.

    Returns:
      The client instance
    """
    self._client = httpx.AsyncClient(
      base_url=self.auth.api_base_url,
      headers=self.auth.get_auth_header(),
      timeout=self._timeout,
    )
    logger.debug("YouTrack client initialized with base URL: %s", self.auth.api_base_url)
    return self

  async def __aexit__(
    self,
    exc_type: type[BaseException] | None,
    exc_val: BaseException | None,
    exc_tb: TracebackType | None,
  ) -> None:
    """Exit the async context manager.

    Args:
      exc_type: The exception type, if any
      exc_val: The exception value, if any
      exc_tb: The exception traceback, if any
    """
    if self._client is not None:
      await self._client.aclose()
      logger.debug("YouTrack client closed")

  @property
  def client(self) -> httpx.AsyncClient:
    """Get the underlying httpx client.

    Returns:
      The httpx AsyncClient instance

    Raises:
      RuntimeError: If the client is not initialized (context manager not entered)
    """
    if self._client is None:
      raise RuntimeError("Client not initialized. Use 'async with' context manager.")
    return self._client

  async def get(self, path: str, **kwargs: Any) -> httpx.Response:
    """Perform a GET request.

    Args:
      path: The API path (relative to base URL)
      **kwargs: Additional arguments to pass to httpx.get()

    Returns:
      The HTTP response

    Raises:
      httpx.HTTPError: If the request fails
    """
    logger.debug("GET %s", path)
    response = await self.client.get(path, **kwargs)
    response.raise_for_status()
    return response
