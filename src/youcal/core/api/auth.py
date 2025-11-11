"""Authentication configuration for YouTrack API."""

from dataclasses import dataclass


@dataclass(frozen=True)
class YouTrackAuth:
  """Authentication information for YouTrack API.

  Attributes:
    base_url: The base URL of the YouTrack instance (e.g., "https://youtrack.example.com")
    bearer_token: The permanent token for API authentication
  """

  base_url: str
  bearer_token: str

  def get_auth_header(self) -> dict[str, str]:
    """Generate the Authorization header for API requests.

    Returns:
      A dictionary with the Authorization header
    """
    return {"Authorization": f"Bearer {self.bearer_token}"}

  @property
  def api_base_url(self) -> str:
    """Get the full API base URL.

    Returns:
      The base URL with trailing slash removed
    """
    return self.base_url.rstrip("/")
