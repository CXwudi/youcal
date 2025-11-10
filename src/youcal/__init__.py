"""YouCal - Export YouTrack issues to iCalendar format."""

from importlib.metadata import PackageNotFoundError, version

from youcal.core import IssuesApi, UsersApi, YouTrackAuth, YouTrackClient, YouTrackIssue

try:
  __version__ = version("youcal")
except PackageNotFoundError:
  # package is not installed
  __version__ = "unknown"

__all__ = [
  "__version__",
  "YouTrackAuth",
  "YouTrackClient",
  "IssuesApi",
  "UsersApi",
  "YouTrackIssue",
]
