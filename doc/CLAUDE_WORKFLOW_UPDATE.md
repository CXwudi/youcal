# Claude Code CI Workflow Update Instructions

## Overview
This document contains the required changes to `.github/workflows/claude.yml` to add Python and uv support.

## Important Note
Claude Code cannot directly modify workflow files due to GitHub App permissions. Please apply these changes manually.

## Required Changes

Add the following steps after the "Give permission to gradlew" step (after line 40):

```yaml
      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version-file: '.python-version'

      - name: Install uv
        uses: astral-sh/setup-uv@v4
        with:
          enable-cache: true

      - name: Install Python dependencies
        run: uv sync --all-extras
```

## Complete Updated Workflow

Here's what the complete workflow should look like after the changes:

```yaml
name: Claude Code

on:
  issue_comment:
    types: [created]
  pull_request_review_comment:
    types: [created]
  issues:
    types: [opened, assigned]
  pull_request_review:
    types: [submitted]

jobs:
  claude:
    if: |
      (github.event_name == 'issue_comment' && contains(github.event.comment.body, '@claude')) ||
      (github.event_name == 'pull_request_review_comment' && contains(github.event.comment.body, '@claude')) ||
      (github.event_name == 'pull_request_review' && contains(github.event.review.body, '@claude')) ||
      (github.event_name == 'issues' && (contains(github.event.issue.body, '@claude') || contains(github.event.issue.title, '@claude')))
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: read
      issues: read
      id-token: write
      actions: read # Required for Claude to read CI results on PRs
    steps:
      - name: Checkout repository
        uses: actions/checkout@v5
        with:
          fetch-depth: 1

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Give permission to gradlew
        run: chmod +x gradlew

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version-file: '.python-version'

      - name: Install uv
        uses: astral-sh/setup-uv@v4
        with:
          enable-cache: true

      - name: Install Python dependencies
        run: uv sync --all-extras

      - name: Run Claude Code
        id: claude
        uses: anthropics/claude-code-action@v1
        with:
          anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}

          # This is an optional setting that allows Claude to read CI results on PRs
          additional_permissions: |
            actions: read

          # Optional: Give a custom prompt to Claude. If this is not specified, Claude will perform the instructions specified in the comment that tagged it.
          # prompt: 'Update the pull request description to include a summary of changes.'

          # Optional: Add claude_args to customize behavior and configuration
          # See https://github.com/anthropics/claude-code-action/blob/main/docs/usage.md
          # or https://docs.claude.com/en/docs/claude-code/cli-reference for available options
          claude_args: '--allowed-tools Bash(gh pr:*) --allowed-tools Bash(uv*)'
```

## Summary of Changes

1. **Added Python setup**: Uses `actions/setup-python@v5` with version specified in `.python-version` file
2. **Added uv installation**: Uses `astral-sh/setup-uv@v4` with caching enabled
3. **Added dependency installation**: Runs `uv sync --all-extras` to install all dependencies including dev dependencies

## Why These Changes?

- The workflow already has Java/Gradle setup for the existing Kotlin codebase
- Adding Python/uv support allows Claude to work with both the existing Kotlin code and the new Python code
- The `uv sync --all-extras` command installs all dependencies defined in `pyproject.toml`, including dev dependencies for testing and linting
- uv's caching is enabled to speed up subsequent workflow runs
