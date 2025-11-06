"""YouTrack Issues API."""

from typing import Optional, Iterator
import logging


logger = logging.getLogger(__name__)


class IssuesApi:
    """API for interacting with YouTrack issues."""

    def __init__(self, client):
        """Initialize Issues API.

        Args:
            client: YouTrackClient instance
        """
        self.client = client

    def get_issues(
        self,
        query: str,
        fields: str,
        custom_fields: Optional[list[str]] = None,
        skip: int = 0,
        top: int = 100,
    ) -> list[dict]:
        """Get issues matching a query.

        Args:
            query: YouTrack search query
            fields: Comma-separated list of fields to retrieve
            custom_fields: List of custom field names to include
            skip: Number of issues to skip (for pagination)
            top: Maximum number of issues to return

        Returns:
            List of issue data as dictionaries
        """
        params = {
            "query": query,
            "fields": fields,
            "$skip": skip,
            "$top": top,
        }

        if custom_fields:
            params["customFields"] = custom_fields

        return self.client.get("issues", params)

    def get_issues_lazily(
        self,
        query: str,
        fields: list[str],
        custom_fields: list[str],
        start_at: int = 0,
        page_size: int = 100,
    ) -> Iterator[dict]:
        """Get issues lazily with automatic pagination.

        This method returns an iterator that automatically fetches
        additional pages as needed.

        Args:
            query: YouTrack search query
            fields: List of field names to retrieve
            custom_fields: List of custom field names to include
            start_at: Starting index for pagination
            page_size: Number of issues per page

        Yields:
            Individual issue data as dictionaries
        """
        skip = start_at
        buffer = []
        still_has_more = True

        while still_has_more:
            # Fetch issues if buffer is empty
            if not buffer:
                logger.debug(
                    f"Reading issue list `{query}` starting from {skip} "
                    f"of amount {page_size}"
                )

                fields_str = ",".join(fields)
                custom_fields_param = custom_fields if custom_fields else None

                partial_list = self.get_issues(
                    query=query,
                    fields=fields_str,
                    custom_fields=custom_fields_param,
                    skip=skip,
                    top=page_size,
                )

                buffer.extend(partial_list)
                skip += page_size
                still_has_more = len(partial_list) == page_size

                if not still_has_more:
                    logger.debug(f"No more issues to read from `{query}`")

            # Yield issues from buffer
            while buffer:
                yield buffer.pop(0)

            # Break if no more pages to fetch
            if not still_has_more:
                break
