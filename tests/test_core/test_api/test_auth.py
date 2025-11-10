"""Tests for authentication module."""

import pytest

from youcal.core.api.auth import YouTrackAuth


def test_youtrack_auth_initialization() -> None:
  """Test YouTrackAuth can be initialized with base URL and token."""
  auth = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="test-token-123",
  )

  assert auth.base_url == "https://youtrack.example.com"
  assert auth.bearer_token == "test-token-123"


def test_youtrack_auth_get_auth_header() -> None:
  """Test that auth header is properly formatted."""
  auth = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="test-token-123",
  )

  header = auth.get_auth_header()

  assert header == {"Authorization": "Bearer test-token-123"}


def test_youtrack_auth_api_base_url() -> None:
  """Test that trailing slashes are removed from base URL."""
  auth_with_slash = YouTrackAuth(
    base_url="https://youtrack.example.com/",
    bearer_token="token",
  )
  auth_without_slash = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="token",
  )

  assert auth_with_slash.api_base_url == "https://youtrack.example.com"
  assert auth_without_slash.api_base_url == "https://youtrack.example.com"


def test_youtrack_auth_is_immutable() -> None:
  """Test that YouTrackAuth is frozen (immutable)."""
  auth = YouTrackAuth(
    base_url="https://youtrack.example.com",
    bearer_token="token",
  )

  with pytest.raises(AttributeError):
    auth.base_url = "https://different.com"  # type: ignore[misc]

  with pytest.raises(AttributeError):
    auth.bearer_token = "different-token"  # type: ignore[misc]
