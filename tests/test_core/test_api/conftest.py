"""Pytest configuration and fixtures for API tests."""

import os

import pytest
import pytest_asyncio

from youcal.core.api.auth import YouTrackAuth
from youcal.core.api.client import YouTrackClient


def get_youtrack_credentials() -> tuple[str, str] | None:
  """Get YouTrack credentials from environment variables.

  Returns:
    Tuple of (base_url, token) if both are set, None otherwise
  """
  base_url = os.getenv("YOUTRACK_URL")
  token = os.getenv("YOUTRACK_TOKEN")

  if not base_url or not token:
    return None

  return base_url, token


def is_youtrack_configured() -> bool:
  """Check if YouTrack credentials are configured.

  Returns:
    True if YOUTRACK_URL and YOUTRACK_TOKEN are set
  """
  return get_youtrack_credentials() is not None


# Skip all tests in this module if YouTrack is not configured
pytestmark = pytest.mark.skipif(
  not is_youtrack_configured(),
  reason="YOUTRACK_URL and YOUTRACK_TOKEN environment variables not set",
)


@pytest.fixture
def youtrack_auth() -> YouTrackAuth:
  """Provide YouTrack authentication configuration.

  Returns:
    YouTrackAuth instance with credentials from environment variables
  """
  credentials = get_youtrack_credentials()
  if credentials is None:
    pytest.skip("YouTrack credentials not configured")

  base_url, token = credentials
  return YouTrackAuth(base_url=base_url, bearer_token=token)


@pytest_asyncio.fixture
async def youtrack_client(youtrack_auth: YouTrackAuth) -> YouTrackClient:
  """Provide a YouTrack client instance.

  Args:
    youtrack_auth: Authentication configuration fixture

  Yields:
    Configured YouTrackClient instance
  """
  async with YouTrackClient(youtrack_auth) as client:
    yield client
