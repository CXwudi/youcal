"""CLI entry point for YouCal."""

import click


@click.command()
def cli() -> None:
    """YouCal - Export YouTrack issues to iCalendar format."""
    click.echo("YouCal CLI - Coming soon!")
    pass
    click.echo("YouCal CLI - Coming soon!")


if __name__ == "__main__":
    cli()
