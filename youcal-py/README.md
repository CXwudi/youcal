# YouCal Python Implementation

Python rewrite of YouCal - a tool to export YouTrack issues to iCalendar format.

## Project Structure

This implementation follows a modular architecture:

- **youcal_core**: Core functionality that can be used standalone or in serverless environments
  - `api/`: YouTrack API client implementation
    - `client.py`: Base HTTP client
    - `users.py`: Users API
    - `issues.py`: Issues API
- **youcal_cli**: Command-line interface (to be implemented)

## Requirements

- Python 3.11 or higher

## Installation

```bash
pip install -e .
```

For development:

```bash
pip install -e ".[dev]"
```

## API Client Usage

```python
from youcal_core.api import YouTrackClient, UsersApi, IssuesApi

# Initialize client
with YouTrackClient(
    base_url="https://youtrack.example.com",
    token="your-api-token"
) as client:
    # Get user timezone
    users_api = UsersApi(client)
    timezone = users_api.get_zone_id_of_user()
    print(f"User timezone: {timezone}")

    # Get issues
    issues_api = IssuesApi(client)
    issues = issues_api.get_issues(
        query="project: MyProject",
        fields="id,summary,created"
    )
    print(f"Found {len(issues)} issues")

    # Use lazy iteration for large result sets
    for issue in issues_api.get_issues_lazily(
        query="project: MyProject",
        fields=["id", "summary"],
        custom_fields=[],
        page_size=50
    ):
        print(f"Issue: {issue['id']} - {issue['summary']}")
```

## Development Status

- [x] API client implementation (Users, Issues)
- [ ] iCalendar generation (core functionality)
- [ ] CLI interface
- [ ] Configuration management
- [ ] Tests
