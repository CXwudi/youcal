"""Custom exceptions for iCalendar mapping."""


class MappingException(Exception):
  """Exception raised when a YouTrack issue cannot be mapped to a VEvent.

  This exception indicates that the issue data from YouTrack is unmappable,
  e.g., missing required date field value. The application should skip
  this issue and continue processing others.

  Attributes:
    message: Explanation of what went wrong
    issue_id: The readable ID of the issue that failed (optional)
  """

  def __init__(self, message: str, issue_id: str | None = None) -> None:
    """Initialize the exception.

    Args:
      message: Explanation of the mapping failure
      issue_id: The readable issue ID (e.g., "PROJECT-123")
    """
    self.issue_id = issue_id
    full_message = f"[{issue_id}] {message}" if issue_id else message
    super().__init__(full_message)
