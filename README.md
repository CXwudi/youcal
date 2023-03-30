# YouCal

YouTrack + iCal = YouCal

Inspired by YouTrack Due Day Calendar, export YouTrack Issue Due to iCal so that other calendar services can subscribe
through iCalendar, using [iCal4j](https://github.com/ical4j/ical4j) library.

This repo is also the remastered version of the original [youcal](https://github.com/sshipway/youcal) (which is no
longer working for me)

For JetBrains Staffs: If you want to reuse some part of this code, just make sure you credit a link to this repo in your
own repo

## Common Prerequisites

- Java 17
    - Optionally GraalVM 22.3.0 above with `native-image` installed

## Documentation

Click on each module for more information:

Application modules:

- [yc-app-cli](yc-app-cli/README.md): The CLI application to export YouTrack Issues from a search query to .ics file.

Library modules:

- [yc-apiclient-youtrack](yc-apiclient-youtrack): The YouTrack API Client written in Spring declarative HTTP client
  coming from Spring 6
- [yc-core-ical](yc-core-ical): The core processing iCal module, can transform one YouTrack Issue into
  one [`VEvent`](https://icalendar.org/iCalendar-RFC-5545/3-6-1-event-component.html),
  using [iCal4j](https://github.com/ical4j/ical4j) library

## Current Progress

- [x] design
- [x] minimal development
- [x] documentation (only for the CLI app)
- [ ] further development
    - So far, this app only supports mapping an YouTrack issue with one date-time/date field into a one-day event in
      ical
      ([`VEvent`](https://icalendar.org/iCalendar-RFC-5545/3-6-1-event-component.html) with a `DATE` value type
      of `DTSTART` property, and no `DTEND` or `DURATION` property).
      However, a new design at #7 has been proposed to support more complex mapping,
      and if more people are interested in this project, I will implement it.
