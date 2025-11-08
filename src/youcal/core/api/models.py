"""YouTrack API models."""

from typing import Any

from pydantic import BaseModel, Field


class YouTrackAuthInfo(BaseModel):
    """Authentication information for YouTrack API."""

    base_url: str = Field(..., description="Base URL of YouTrack instance (e.g., https://youtrack.example.com)")
    bearer_token: str = Field(..., description="Bearer token for authentication")

    def get_api_base_url(self) -> str:
        """Get the API base URL."""
        return f"{self.base_url.rstrip('/')}/api"


class YouTrackIssue(BaseModel):
    """Represents a YouTrack issue.

    This is a flexible model that can hold any JSON structure returned by YouTrack API.
    The actual fields depend on what's requested via the 'fields' parameter.
    """

    model_config = {"extra": "allow"}

    def __getitem__(self, key: str) -> Any:
        """Allow dictionary-style access to fields."""
        return getattr(self, key, None)

    def get(self, key: str, default: Any = None) -> Any:
        """Get a field value with a default."""
        return getattr(self, key, default)


class YouTrackUserProfile(BaseModel):
    """Represents a YouTrack user profile.

    This is a flexible model that can hold any JSON structure returned by YouTrack API.
    """

    model_config = {"extra": "allow"}

    def __getitem__(self, key: str) -> Any:
        """Allow dictionary-style access to fields."""
        return getattr(self, key, None)

    def get(self, key: str, default: Any = None) -> Any:
        """Get a field value with a default."""
        return getattr(self, key, default)
