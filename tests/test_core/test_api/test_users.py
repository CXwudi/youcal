"""Tests for Users API."""

from zoneinfo import ZoneInfo

import pytest

from youcal.core.api.models import UserProfile
from youcal.core.api.users import UsersApi


@pytest.fixture
def users_api(youtrack_client) -> UsersApi:  # type: ignore[no-untyped-def]
  """Provide UsersApi instance.

  Args:
    youtrack_client: The YouTrack client fixture

  Returns:
    UsersApi instance
  """
  return UsersApi(youtrack_client)


@pytest.mark.asyncio
async def test_get_general_profile_me(users_api) -> None:  # type: ignore[no-untyped-def]
  """Test getting current user's general profile."""
  profile = await users_api.get_general_profile(user_id="me")

  assert isinstance(profile, UserProfile)
  assert profile.timezone is not None
  assert profile.timezone.id, "Timezone ID should not be empty"


@pytest.mark.asyncio
async def test_get_general_profile_with_fields(users_api) -> None:  # type: ignore[no-untyped-def]
  """Test getting profile with specific fields."""
  profile = await users_api.get_general_profile(
    user_id="me",
    fields="timezone(id,offset,presentation)",
  )

  assert isinstance(profile, UserProfile)
  assert profile.timezone.id


@pytest.mark.asyncio
async def test_get_timezone_me(users_api) -> None:  # type: ignore[no-untyped-def]
  """Test getting current user's timezone."""
  timezone = await users_api.get_timezone(user_id="me")

  assert isinstance(timezone, ZoneInfo)
  # Verify it's a valid timezone by checking it has a key
  assert timezone.key, "ZoneInfo should have a valid key"


@pytest.mark.asyncio
async def test_get_timezone_default(users_api) -> None:  # type: ignore[no-untyped-def]
  """Test getting timezone with default user_id parameter."""
  timezone = await users_api.get_timezone()

  assert isinstance(timezone, ZoneInfo)
  assert timezone.key


@pytest.mark.asyncio
async def test_timezone_is_valid(users_api) -> None:  # type: ignore[no-untyped-def]
  """Test that returned timezone is a valid ZoneInfo that can be used."""
  timezone = await users_api.get_timezone()

  # Should be able to use the timezone for datetime operations
  from datetime import datetime

  now = datetime.now(tz=timezone)
  assert now.tzinfo is not None
  assert now.tzinfo == timezone
