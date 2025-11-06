"""YouCal - Export YouTrack issues to iCalendar format."""

from importlib.metadata import PackageNotFoundError, version

try:
    __version__ = version("youcal")
except PackageNotFoundError:
    # package is not installed
    __version__ = "unknown"
