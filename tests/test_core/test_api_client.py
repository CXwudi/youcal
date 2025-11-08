"""Tests for YouTrack API client."""

from unittest.mock import AsyncMock, MagicMock, patch
from zoneinfo import ZoneInfo

import httpx
import pytest

from youcal.core.api import YouTrackAuthInfo, YouTrackClient


@pytest.fixture
def auth_info() -> YouTrackAuthInfo:
    """Create test auth info."""
    return YouTrackAuthInfo(
        base_url="https://youtrack.example.com",
        bearer_token="test-token-123",
    )


@pytest.fixture
def mock_response() -> MagicMock:
    """Create a mock HTTP response."""
    response = MagicMock(spec=httpx.Response)
    response.raise_for_status = MagicMock()
    return response


class TestYouTrackAuthInfo:
    """Test YouTrackAuthInfo model."""

    def test_get_api_base_url(self, auth_info: YouTrackAuthInfo) -> None:
        """Test API base URL generation."""
        assert auth_info.get_api_base_url() == "https://youtrack.example.com/api"

    def test_get_api_base_url_with_trailing_slash(self) -> None:
        """Test API base URL generation with trailing slash."""
        auth_info = YouTrackAuthInfo(
            base_url="https://youtrack.example.com/",
            bearer_token="test-token",
        )
        assert auth_info.get_api_base_url() == "https://youtrack.example.com/api"


class TestYouTrackClient:
    """Test YouTrackClient."""

    @pytest.mark.asyncio
    async def test_context_manager(self, auth_info: YouTrackAuthInfo) -> None:
        """Test client context manager."""
        async with YouTrackClient(auth_info) as client:
            assert client._client is not None
            assert isinstance(client._client, httpx.AsyncClient)

    @pytest.mark.asyncio
    async def test_get_issues(self, auth_info: YouTrackAuthInfo, mock_response: MagicMock) -> None:
        """Test getting issues."""
        mock_response.json.return_value = [
            {"id": "ISSUE-1", "summary": "Test issue 1"},
            {"id": "ISSUE-2", "summary": "Test issue 2"},
        ]

        async with YouTrackClient(auth_info) as client:
            with patch.object(client.client, "get", new_callable=AsyncMock, return_value=mock_response):
                issues = await client.get_issues(
                    query="project: TEST",
                    fields=["id", "summary"],
                    skip=0,
                    top=10,
                )

                assert len(issues) == 2
                assert issues[0]["id"] == "ISSUE-1"
                assert issues[1]["id"] == "ISSUE-2"

    @pytest.mark.asyncio
    async def test_get_issues_with_custom_fields(
        self, auth_info: YouTrackAuthInfo, mock_response: MagicMock
    ) -> None:
        """Test getting issues with custom fields."""
        mock_response.json.return_value = [{"id": "ISSUE-1"}]

        async with YouTrackClient(auth_info) as client:
            with patch.object(client.client, "get", new_callable=AsyncMock, return_value=mock_response) as mock_get:
                await client.get_issues(
                    query="project: TEST",
                    fields=["id", "summary"],
                    custom_fields=["customField1", "customField2"],
                    skip=0,
                    top=10,
                )

                # Verify custom fields were passed
                call_args = mock_get.call_args
                assert "customFields" in call_args[1]["params"]
                assert call_args[1]["params"]["customFields"] == ["customField1", "customField2"]

    @pytest.mark.asyncio
    async def test_get_issues_lazy(self, auth_info: YouTrackAuthInfo, mock_response: MagicMock) -> None:
        """Test lazy issue retrieval with pagination."""
        # Mock two pages: first with 2 items, second with 1 item (less than page size)
        mock_response.json.side_effect = [
            [{"id": "ISSUE-1"}, {"id": "ISSUE-2"}],
            [{"id": "ISSUE-3"}],
        ]

        async with YouTrackClient(auth_info) as client:
            with patch.object(client.client, "get", new_callable=AsyncMock, return_value=mock_response):
                issues = []
                async for issue in client.get_issues_lazy(
                    query="project: TEST",
                    fields=["id"],
                    page_size=2,
                ):
                    issues.append(issue)

                assert len(issues) == 3
                assert issues[0]["id"] == "ISSUE-1"
                assert issues[1]["id"] == "ISSUE-2"
                assert issues[2]["id"] == "ISSUE-3"

    @pytest.mark.asyncio
    async def test_get_user_profile(self, auth_info: YouTrackAuthInfo, mock_response: MagicMock) -> None:
        """Test getting user profile."""
        mock_response.json.return_value = {
            "timezone": {
                "id": "America/New_York",
                "offset": -18000000,
                "presentation": "EST",
            }
        }

        async with YouTrackClient(auth_info) as client:
            with patch.object(client.client, "get", new_callable=AsyncMock, return_value=mock_response):
                profile = await client.get_user_profile(user_id="me")

                assert "timezone" in profile
                assert profile["timezone"]["id"] == "America/New_York"

    @pytest.mark.asyncio
    async def test_get_user_timezone(self, auth_info: YouTrackAuthInfo, mock_response: MagicMock) -> None:
        """Test getting user timezone."""
        mock_response.json.return_value = {
            "timezone": {
                "id": "America/New_York",
                "offset": -18000000,
                "presentation": "EST",
            }
        }

        async with YouTrackClient(auth_info) as client:
            with patch.object(client.client, "get", new_callable=AsyncMock, return_value=mock_response):
                timezone = await client.get_user_timezone(user_id="me")

                assert isinstance(timezone, ZoneInfo)
                assert str(timezone) == "America/New_York"

    @pytest.mark.asyncio
    async def test_client_not_initialized_error(self, auth_info: YouTrackAuthInfo) -> None:
        """Test error when client not initialized."""
        client = YouTrackClient(auth_info)
        with pytest.raises(RuntimeError, match="Client not initialized"):
            _ = client.client
