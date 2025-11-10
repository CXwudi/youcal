"""Users API for YouTrack."""

import logging
from zoneinfo import ZoneInfo

from youcal.core.api.client import YouTrackClient
from youcal.core.api.models import UserProfile

logger = logging.getLogger(__name__)


class UsersApi:
  """API client for YouTrack users.

  This class provides methods to retrieve user profile information,
  including timezone settings.

  Attributes:
    client: The YouTrack HTTP client
  """

  def __init__(self, client: YouTrackClient) -> None:
    """Initialize the Users API.

    Args:
      client: The YouTrack client instance
    """
    self._client = client

  async def get_general_profile(
    self,
    user_id: str = "me",
    fields: str = "timezone(id,offset,presentation)",
  ) -> UserProfile:
    """Get a user's general profile.

    Args:
      user_id: The user ID to query (default: "me" for current user)
      fields: Comma-separated list of fields to retrieve

    Returns:
      The user's profile information

    Raises:
      httpx.HTTPError: If the API request fails
    """
    logger.debug("Fetching general profile for user '%s'", user_id)

    response = await self._client.get(
      f"/api/users/{user_id}/profiles/general",
      params={"fields": fields},
    )
    response_data = response.json()

    # Parse response into Pydantic model
    profile = UserProfile.model_validate(response_data)
    logger.debug("Retrieved profile for user '%s' with timezone: %s", user_id, profile.timezone.id)

    return profile

  async def get_timezone(self, user_id: str = "me") -> ZoneInfo:
    """Get the timezone for a user.

    Args:
      user_id: The user ID to query (default: "me" for current user)

    Returns:
      The user's timezone as a ZoneInfo object

    Raises:
      httpx.HTTPError: If the API request fails
      ZoneInfoNotFoundError: If the timezone ID is invalid
    """
    profile = await self.get_general_profile(user_id)
    timezone_id = profile.timezone.id

    logger.debug("User '%s' timezone: %s", user_id, timezone_id)
    return ZoneInfo(timezone_id)
