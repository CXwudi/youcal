"""Tests for Issues API."""

import pytest

from youcal.core.api.client import YouTrackClient
from youcal.core.api.issues import IssuesApi
from youcal.core.api.models import YouTrackIssue


@pytest.fixture
def issues_api(youtrack_client: YouTrackClient) -> IssuesApi:
  """Provide IssuesApi instance.

  Args:
    youtrack_client: The YouTrack client fixture

  Returns:
    IssuesApi instance
  """
  return IssuesApi(youtrack_client)


@pytest.mark.asyncio
async def test_get_issues_basic(issues_api: IssuesApi) -> None:
  """Test basic issue retrieval with minimal query.

  This test retrieves a small number of issues to verify the API works.
  """
  issues = await issues_api.get_issues(
    query="",  # Empty query searches all issues
    fields=["id", "idReadable", "summary"],
    skip=0,
    top=5,
  )

  assert isinstance(issues, list)
  assert len(issues) <= 5

  # Verify each issue has required fields
  for issue in issues:
    assert isinstance(issue, YouTrackIssue)
    assert issue.id
    assert issue.id_readable
    assert issue.summary


@pytest.mark.asyncio
async def test_get_issues_with_custom_fields(issues_api: IssuesApi) -> None:
  """Test issue retrieval with custom fields."""
  issues = await issues_api.get_issues(
    query="",
    fields=["id", "idReadable", "summary", "customFields(name,id,value(name,id))"],
    custom_fields=["Due Date"],
    skip=0,
    top=3,
  )

  assert isinstance(issues, list)
  assert len(issues) <= 3

  # Custom fields should be present
  for issue in issues:
    assert hasattr(issue, "custom_fields")
    assert isinstance(issue.custom_fields, list)


@pytest.mark.asyncio
async def test_get_issues_pagination(issues_api: IssuesApi) -> None:
  """Test pagination by fetching two different pages."""
  page_size = 3

  # Get first page
  page1 = await issues_api.get_issues(
    query="",
    fields=["id", "idReadable", "summary"],
    skip=0,
    top=page_size,
  )

  # Get second page
  page2 = await issues_api.get_issues(
    query="",
    fields=["id", "idReadable", "summary"],
    skip=page_size,
    top=page_size,
  )

  # If we have enough issues, verify pages are different
  if len(page1) == page_size and len(page2) > 0:
    page1_ids = {issue.id for issue in page1}
    page2_ids = {issue.id for issue in page2}
    assert page1_ids != page2_ids, "Pages should contain different issues"


@pytest.mark.asyncio
async def test_get_issues_empty_result(issues_api: IssuesApi) -> None:
  """Test that querying with very restrictive criteria returns empty list."""
  # Query for issues with a very specific summary that doesn't exist
  issues = await issues_api.get_issues(
    query='summary: "NONEXISTENT_ISSUE_SUMMARY_XYZABC123"',
    fields=["id", "idReadable", "summary"],
    skip=0,
    top=10,
  )

  assert isinstance(issues, list)
  assert len(issues) == 0, "Should return empty list for non-existent summary"


@pytest.mark.asyncio
async def test_get_issues_lazy_basic(issues_api: IssuesApi) -> None:
  """Test lazy issue retrieval."""
  count = 0
  max_issues = 10

  async for issue in issues_api.get_issues_lazy(
    query="",
    fields=["id", "idReadable", "summary"],
    page_size=3,
  ):
    assert isinstance(issue, YouTrackIssue)
    assert issue.id
    assert issue.id_readable

    count += 1
    if count >= max_issues:
      break  # Don't fetch all issues, just test the mechanism

  assert count > 0, "Should retrieve at least some issues"


@pytest.mark.asyncio
async def test_get_issues_lazy_pagination(issues_api: IssuesApi) -> None:
  """Test that lazy loading properly handles pagination."""
  page_size = 2
  collected_issues = []
  max_issues = 5

  async for issue in issues_api.get_issues_lazy(
    query="",
    fields=["id", "idReadable", "summary"],
    page_size=page_size,
  ):
    collected_issues.append(issue)
    if len(collected_issues) >= max_issues:
      break

  # Verify we got issues
  assert len(collected_issues) > 0

  # Verify no duplicates (each issue should be unique)
  issue_ids = [issue.id for issue in collected_issues]
  assert len(issue_ids) == len(set(issue_ids)), "Should not have duplicate issues"


@pytest.mark.asyncio
async def test_get_issues_lazy_with_start_at(issues_api: IssuesApi) -> None:
  """Test lazy loading with custom starting offset."""
  start_at = 2
  collected_issues = []
  max_issues = 3

  async for issue in issues_api.get_issues_lazy(
    query="",
    fields=["id", "idReadable", "summary"],
    start_at=start_at,
    page_size=2,
  ):
    collected_issues.append(issue)
    if len(collected_issues) >= max_issues:
      break

  # Should get issues starting from offset
  assert len(collected_issues) > 0


@pytest.mark.asyncio
async def test_get_issues_lazy_empty_query(issues_api: IssuesApi) -> None:
  """Test lazy loading with query that returns no results."""
  count = 0

  async for issue in issues_api.get_issues_lazy(
    query='summary: "NONEXISTENT_ISSUE_SUMMARY_XYZABC123"',
    fields=["id", "idReadable", "summary"],
    page_size=5,
  ):
    count += 1

  assert count == 0, "Should not yield any issues for this specific summary query"
