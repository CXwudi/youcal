"""Tests for YouTrack client."""

import pytest

from youcal.core.api.client import YouTrackClient


@pytest.mark.asyncio
async def test_client_context_manager(youtrack_auth) -> None:  # type: ignore[no-untyped-def]
  """Test that client can be used as an async context manager."""
  async with YouTrackClient(youtrack_auth) as client:
    assert client._client is not None
    assert client.client is not None


@pytest.mark.asyncio
async def test_client_not_initialized_error(youtrack_auth) -> None:  # type: ignore[no-untyped-def]
  """Test that accessing client before initialization raises error."""
  client = YouTrackClient(youtrack_auth)

  with pytest.raises(RuntimeError, match="Client not initialized"):
    _ = client.client


@pytest.mark.asyncio
async def test_client_get_request(youtrack_client) -> None:  # type: ignore[no-untyped-def]
  """Test that client can make a GET request to YouTrack API.

  This test verifies basic connectivity to the YouTrack instance.
  """
  # Test a simple endpoint that should always work
  response = await youtrack_client.get("/api/users/me", params={"fields": "id"})

  assert response.status_code == 200
  data = response.json()
  assert "id" in data


@pytest.mark.asyncio
async def test_client_auth_header(youtrack_auth) -> None:  # type: ignore[no-untyped-def]
  """Test that client includes auth header in requests."""
  async with YouTrackClient(youtrack_auth) as client:
    # Check that the auth header is set on the client
    assert "Authorization" in client.client.headers
    assert client.client.headers["Authorization"] == f"Bearer {youtrack_auth.bearer_token}"


@pytest.mark.asyncio
async def test_client_base_url(youtrack_auth) -> None:  # type: ignore[no-untyped-def]
  """Test that client uses correct base URL."""
  async with YouTrackClient(youtrack_auth) as client:
    assert str(client.client.base_url) == youtrack_auth.api_base_url
