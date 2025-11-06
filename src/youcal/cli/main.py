"""CLI entry point for YouCal."""

import click


@click.command()
@click.version_option()
def cli() -> None:
    """YouCal - Export YouTrack issues to iCalendar format."""
    click.echo("YouCal CLI - Coming soon!")


if __name__ == "__main__":
    cli()
