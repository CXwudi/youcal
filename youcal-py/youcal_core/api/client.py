"""YouTrack API client base."""

import httpx
from typing import Optional


class YouTrackClient:
    """Base client for YouTrack API interactions."""

    def __init__(
        self,
        base_url: str,
        token: str,
        timeout: float = 30.0,
    ):
        """Initialize YouTrack client.

        Args:
            base_url: YouTrack instance base URL (e.g., 'https://youtrack.example.com')
            token: Authentication token for YouTrack API
            timeout: Request timeout in seconds
        """
        self.base_url = base_url.rstrip('/')
        self.api_base = f"{self.base_url}/api"

        self._client = httpx.Client(
            headers={
                "Authorization": f"Bearer {token}",
                "Accept": "application/json",
                "Content-Type": "application/json",
            },
            timeout=timeout,
        )

    def get(self, endpoint: str, params: Optional[dict] = None) -> dict:
        """Make a GET request to the YouTrack API.

        Args:
            endpoint: API endpoint path (relative to /api)
            params: Query parameters

        Returns:
            Response JSON as dictionary

        Raises:
            httpx.HTTPError: If the request fails
        """
        url = f"{self.api_base}/{endpoint.lstrip('/')}"
        response = self._client.get(url, params=params)
        response.raise_for_status()
        return response.json()

    def close(self):
        """Close the HTTP client."""
        self._client.close()

    def __enter__(self):
        """Context manager entry."""
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit."""
        self.close()
