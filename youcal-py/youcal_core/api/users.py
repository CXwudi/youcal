"""YouTrack Users API."""

from typing import Optional
from zoneinfo import ZoneInfo


class UsersApi:
    """API for interacting with YouTrack users."""

    def __init__(self, client):
        """Initialize Users API.

        Args:
            client: YouTrackClient instance
        """
        self.client = client

    def get_general_profile(
        self,
        user_id: str = "me",
        fields: str = "id,login,name",
    ) -> dict:
        """Get general profile information for a user.

        Args:
            user_id: User ID or 'me' for current user
            fields: Comma-separated list of fields to retrieve

        Returns:
            User profile data as dictionary
        """
        endpoint = f"users/{user_id}/profiles/general"
        params = {"fields": fields}
        return self.client.get(endpoint, params)

    def get_zone_id_of_user(self, user_id: str = "me") -> ZoneInfo:
        """Get the timezone of a user.

        Args:
            user_id: User ID or 'me' for current user

        Returns:
            ZoneInfo object representing the user's timezone
        """
        response = self.get_general_profile(
            user_id=user_id,
            fields="timezone(id,offset,presentation)"
        )
        zone_id_str = response["timezone"]["id"]
        return ZoneInfo(zone_id_str)
