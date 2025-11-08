"""YouTrack API client."""

import logging
from collections.abc import AsyncIterator
from typing import Any
from zoneinfo import ZoneInfo

import httpx

from .models import YouTrackAuthInfo

logger = logging.getLogger(__name__)


class YouTrackClient:
    """Asynchronous YouTrack API client.

    This client provides methods to interact with the YouTrack REST API,
    including issue querying with pagination and user profile retrieval.
    """

    def __init__(self, auth_info: YouTrackAuthInfo, timeout: float = 30.0) -> None:
        """Initialize the YouTrack client.

        Args:
            auth_info: Authentication information for YouTrack API
            timeout: Request timeout in seconds (default: 30.0)
        """
        self.auth_info = auth_info
        self.api_base_url = auth_info.get_api_base_url()
        self.timeout = timeout
        self._client: httpx.AsyncClient | None = None

    async def __aenter__(self) -> "YouTrackClient":
        """Enter async context manager."""
        self._client = httpx.AsyncClient(
            base_url=self.api_base_url,
            headers={"Authorization": f"Bearer {self.auth_info.bearer_token}"},
            timeout=self.timeout,
        )
        return self

    async def __aexit__(self, exc_type: Any, exc_val: Any, exc_tb: Any) -> None:
        """Exit async context manager."""
        if self._client:
            await self._client.aclose()

    @property
    def client(self) -> httpx.AsyncClient:
        """Get the HTTP client, creating it if necessary."""
        if self._client is None:
            raise RuntimeError("Client not initialized. Use 'async with' context manager.")
        return self._client

    async def get_issues(
        self,
        query: str,
        fields: list[str],
        custom_fields: list[str] | None = None,
        skip: int = 0,
        top: int = 100,
    ) -> list[dict[str, Any]]:
        """Get issues from YouTrack.

        Args:
            query: YouTrack search query
            fields: List of fields to retrieve
            custom_fields: List of custom fields to retrieve (optional)
            skip: Number of issues to skip (for pagination)
            top: Maximum number of issues to retrieve

        Returns:
            List of issue objects as dictionaries
        """
        params: dict[str, Any] = {
            "query": query,
            "fields": ",".join(fields),
            "$skip": skip,
            "$top": top,
        }

        if custom_fields:
            params["customFields"] = custom_fields

        logger.debug(
            "Getting issues with query='%s', skip=%d, top=%d",
            query,
            skip,
            top,
        )

        response = await self.client.get("/issues", params=params)
        response.raise_for_status()
        result: list[dict[str, Any]] = response.json()
        return result

    async def get_issues_lazy(
        self,
        query: str,
        fields: list[str],
        custom_fields: list[str] | None = None,
        start_at: int = 0,
        page_size: int = 100,
    ) -> AsyncIterator[dict[str, Any]]:
        """Get issues lazily with automatic pagination.

        This method yields issues one at a time, automatically fetching new pages
        as needed. This is memory-efficient for large result sets.

        Args:
            query: YouTrack search query
            fields: List of fields to retrieve
            custom_fields: List of custom fields to retrieve (optional)
            start_at: Starting position (default: 0)
            page_size: Number of issues per page (default: 100)

        Yields:
            Issue objects as dictionaries
        """
        skip = start_at
        still_has_more = True

        while still_has_more:
            logger.debug("Reading issue list '%s' starting from %d with page size %d", query, skip, page_size)

            issues = await self.get_issues(
                query=query,
                fields=fields,
                custom_fields=custom_fields,
                skip=skip,
                top=page_size,
            )

            for issue in issues:
                yield issue

            skip += page_size
            still_has_more = len(issues) == page_size

            if not still_has_more:
                logger.debug("No more issues to read from '%s'", query)

    async def get_user_profile(
        self,
        user_id: str = "me",
        fields: str = "timezone(id,offset,presentation)",
    ) -> dict[str, Any]:
        """Get user profile from YouTrack.

        Args:
            user_id: User ID or 'me' for current user (default: 'me')
            fields: Fields to retrieve

        Returns:
            User profile as dictionary
        """
        params = {"fields": fields}

        logger.debug("Getting user profile for user_id='%s'", user_id)

        response = await self.client.get(f"/users/{user_id}/profiles/general", params=params)
        response.raise_for_status()
        result: dict[str, Any] = response.json()
        return result

    async def get_user_timezone(self, user_id: str = "me") -> ZoneInfo:
        """Get timezone for a user.

        Args:
            user_id: User ID or 'me' for current user (default: 'me')

        Returns:
            ZoneInfo object representing the user's timezone
        """
        profile = await self.get_user_profile(user_id, "timezone(id,offset,presentation)")
        timezone_id = profile["timezone"]["id"]
        logger.debug("User %s timezone: %s", user_id, timezone_id)
        return ZoneInfo(timezone_id)
