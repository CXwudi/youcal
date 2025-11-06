"""Basic tests for API client structure."""

import pytest
from youcal_core.api import YouTrackClient, UsersApi, IssuesApi


def test_client_initialization():
    """Test that client can be initialized."""
    client = YouTrackClient(
        base_url="https://test.youtrack.com",
        token="test-token"
    )
    assert client.base_url == "https://test.youtrack.com"
    assert client.api_base == "https://test.youtrack.com/api"
    client.close()


def test_users_api_initialization():
    """Test that UsersApi can be initialized."""
    client = YouTrackClient(
        base_url="https://test.youtrack.com",
        token="test-token"
    )
    users_api = UsersApi(client)
    assert users_api.client == client
    client.close()


def test_issues_api_initialization():
    """Test that IssuesApi can be initialized."""
    client = YouTrackClient(
        base_url="https://test.youtrack.com",
        token="test-token"
    )
    issues_api = IssuesApi(client)
    assert issues_api.client == client
    client.close()


def test_client_context_manager():
    """Test that client works as context manager."""
    with YouTrackClient(
        base_url="https://test.youtrack.com",
        token="test-token"
    ) as client:
        assert client.base_url == "https://test.youtrack.com"
