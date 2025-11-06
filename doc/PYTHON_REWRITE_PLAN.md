# Python Rewrite Plan

This document outlines the comprehensive plan for rewriting YouCal from Kotlin to Python.

## Current Implementation Overview

**Technology Stack:**

- **Language:** Kotlin with Spring Boot
- **Build System:** Gradle with Kotlin DSL
- **Key Dependencies:**
  - Spring Boot (WebFlux for HTTP client, Validation, JSON)
  - iCal4j (for iCalendar file generation)
  - Jackson (JSON processing)
- **Architecture:** Modular design with 3 modules:
  1. `yc-apiclient-youtrack` - YouTrack API client
  2. `yc-core-ical` - Core iCal mapping logic
  3. `yc-app-cli` - CLI application

**Functionality:**

- Queries YouTrack issues via REST API
- Maps YouTrack issues to iCalendar VEvent components
- Exports events to .ics files
- Supports custom field mapping, alarms, and timezone handling
- Pagination support for large result sets
- GraalVM native image compilation capability

## 1. Project Structure

```text
youcal/
├── pyproject.toml              # Project metadata and dependencies (uv-managed)
├── uv.lock                     # Lock file for reproducible installs
├── README.md
├── .gitignore
├── .python-version             # Python version specification for uv
├── src/
│   └── youcal/
│       ├── __init__.py
│       ├── core/               # Core module (for potential serverless use)
│       │   ├── __init__.py
│       │   ├── api/           # YouTrack API client
│       │   │   ├── __init__.py
│       │   │   ├── client.py
│       │   │   └── models.py
│       │   ├── ical/          # iCalendar generation
│       │   │   ├── __init__.py
│       │   │   ├── mapper.py
│       │   │   ├── event.py
│       │   │   ├── alarm.py
│       │   │   └── calendar.py
│       │   └── config/        # Configuration models
│       │       ├── __init__.py
│       │       └── models.py
│       └── cli/               # CLI module
│           ├── __init__.py
│           ├── main.py
│           └── config_loader.py
├── tests/
│   ├── __init__.py
│   ├── test_core/
│   └── test_cli/
└── examples/
    └── config.yaml
```

## 2. Technology Stack

### Package Manager & Build Tool

- **`uv`** - Ultra-fast Python package installer and resolver (replaces pip, pip-tools, and poetry)
  - Benefits: Extremely fast dependency resolution, built in Rust
  - Lock file support for reproducible builds
  - Compatible with standard `pyproject.toml`

### Core Dependencies

- **HTTP Client:** `httpx` (async support, similar to Spring WebFlux)
- **iCalendar:** `icalendar` library (Python equivalent of iCal4j)
- **JSON Processing:** Built-in `json` module + `pydantic` for validation
- **Configuration:** `pydantic-settings` (replaces Spring Boot Configuration Properties)
- **CLI Framework:** `click` or `typer` (for better CLI experience)
- **Async Support:** `asyncio` (built-in)
- **Logging:** `structlog` or built-in `logging`

### Development Dependencies

- **Testing:** `pytest`, `pytest-asyncio`, `pytest-cov`
- **Linting:** `ruff` (fast linter/formatter)
- **Type Checking:** `mypy`

## 3. Module Breakdown

### 3.1 Core Module (`youcal.core`)

This module will be completely independent and can be used in serverless environments (AWS Lambda, Azure Functions, etc.).

#### API Client (`youcal.core.api`)

- `YouTrackClient` class using `httpx.AsyncClient`
- Pagination iterator for lazy loading
- Authentication handling (Bearer token)
- [ ] Implement `YouTrackClient` with basic authentication

- [ ] Implement `YouTrackClient` with Bearer Token authentication

#### iCalendar Generation (`youcal.core.ical`)

- `EventMapper` - Maps YouTrack issues to VEvent
- `AlarmMapper` - Handles alarm/reminder creation
- `DateTimeFieldSetter` - Handles date/time field mapping
- `CalendarBuilder` - Creates iCalendar objects
- Support for timezones using `zoneinfo`

#### Configuration Models (`youcal.core.config`)

- Pydantic models for all configuration
- Validation rules
- Type-safe configuration handling

### 3.2 CLI Module (`youcal.cli`)

#### Main Application

- Entry point using Click or Typer
- Configuration loading from YAML/JSON/env vars
- Progress indicators for long-running operations
- Error handling and user-friendly messages

#### Configuration Loading

- Support multiple config sources (similar to Spring Boot):
  - YAML/JSON files
  - Environment variables
  - Command-line arguments
- Config file import support

## 4. Migration Strategy

### Phase 1: Core API Client (Week 1-2)

- [ ] Set up Python project structure with `pyproject.toml` and `uv`
- [ ] Implement `YouTrackClient` with basic authentication
- [ ] Implement issue querying with pagination
- [ ] Add comprehensive tests
- [ ] Document API usage

### Phase 2: iCalendar Generation (Week 2-3)

- [ ] Implement `EventMapper` and supporting classes
- [ ] Add datetime field mapping logic
- [ ] Implement alarm/reminder support
- [ ] Add timezone handling
- [ ] Test with various YouTrack issue formats

### Phase 3: CLI Application (Week 3-4)

- [ ] Implement CLI using Click/Typer
- [ ] Add configuration loading system
- [ ] Implement main orchestration logic
- [ ] Add progress indicators and logging
- [ ] Handle errors gracefully

### Phase 4: Testing & Documentation (Week 4-5)

- [ ] Write comprehensive unit tests (target >80% coverage)
- [ ] Write integration tests
- [ ] Create user documentation
- [ ] Add examples and usage guides
- [ ] Performance testing

### Phase 5: Packaging & Distribution (Week 5-6)

- [ ] Set up PyPI packaging
- [ ] Create Docker image
- [ ] Add GitHub Actions for CI/CD
- [ ] Consider PyInstaller for standalone binaries
- [ ] Migration guide from Kotlin version

## 5. Key Implementation Considerations

### Using `uv` for Development

```bash
# Initialize project
uv init

# Add dependencies
uv add httpx icalendar pydantic pydantic-settings click

# Add dev dependencies
uv add --dev pytest pytest-asyncio pytest-cov ruff mypy

# Install dependencies
uv sync

# Run commands
uv run pytest
uv run mypy src/
uv run ruff check src/
```

### Async/Await Pattern

```python
async def get_issues(client: YouTrackClient, query: str) -> list[VEvent]:
    return [map_to_event(issue) async for issue in client.get_issues_lazy(query)]
```

### Configuration Example

```python
from pydantic_settings import BaseSettings

class YouCalConfig(BaseSettings):
    youtrack_url: str
    youtrack_token: str
    search_query: str
    output_file: str
    timezone: str = "UTC"

    class Config:
        env_prefix = "YOUCAL_"
        env_file = ".env"
```

### Serverless-Friendly Core

The core module will be designed to work in serverless contexts:

```python
# Lambda handler example
from youcal.core import YouTrackClient, EventMapper, CalendarBuilder

async def lambda_handler(event, context):
    config = parse_config(event)
    client = YouTrackClient(config.youtrack_url, config.token)
    mapper = EventMapper()
    builder = CalendarBuilder()

    issues = await client.get_issues(config.query)
    events = [mapper.map(issue) for issue in issues]
    calendar = builder.build(events)

    return calendar.to_ical()
```

## 6. Feature Parity Checklist

To ensure same functionality:

- [ ] YouTrack API authentication
- [ ] Issue querying with custom fields
- [ ] Pagination support
- [ ] Date/DateTime field mapping
- [ ] Timezone handling
- [ ] Alarm/reminder creation
- [ ] Custom field mapping
- [ ] Summary and description handling
- [ ] Configuration via YAML/JSON/env
- [ ] Configuration validation
- [ ] .ics file output
- [ ] Logging and error handling
- [ ] Async processing for I/O-bound tasks; sequential processing for CPU-bound tasks (acceptable for up to 10,000 items)

## 7. Testing Strategy

### Unit Tests

- Test each core component independently
- Mock external API calls
- Test configuration validation
- Test edge cases and error conditions

### Integration Tests

- Test against YouTrack API (with test account)
- Validate generated .ics files
- Test full end-to-end workflow

### Performance Tests

- Compare performance with Kotlin version
- Test with large result sets
- Measure memory usage

## 8. Benefits of Python Rewrite with `uv`

1. **Easier to maintain** - Python is more accessible than Kotlin
2. **Serverless ready** - Core module designed for AWS Lambda, Azure Functions
3. **Simpler deployment** - No JVM required, smaller Docker images
4. **Better ecosystem** - Rich Python libraries for data processing
5. **Type safety** - Pydantic provides runtime validation
6. **Modern tooling** - Ruff for fast linting, pytest for testing
7. **Ultra-fast dependency management** - `uv` is 10-100x faster than pip/poetry
8. **Reproducible builds** - Lock file ensures consistent environments

## 9. Potential Challenges

1. **iCal4j vs icalendar** - Library differences may require adjustments
2. **Spring Boot configuration** - Need to replicate flexibility
3. **GraalVM native** - Need alternative (PyInstaller) for single binary
4. **Performance** - May need optimization for large datasets

## Summary

This plan provides a roadmap for rewriting YouCal in Python that:

- Maintains the same functionality as the current Kotlin implementation
- Splits the codebase into **core** and **cli** modules as required
- Makes the core module serverless-ready for AWS Lambda, Azure Functions, etc.
- Provides a clear migration strategy with 5 phases
- Recommends modern Python tooling and best practices
- Includes testing and documentation plans
- Uses `uv` for ultra-fast dependency management

The plan analyzes the current Kotlin implementation (~65 source files) and proposes equivalent Python implementations using `httpx` for HTTP, `icalendar` for iCal generation, and `pydantic` for configuration validation.
