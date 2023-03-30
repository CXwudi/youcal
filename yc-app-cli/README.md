# YouCal CLI App

This CLI App simply takes
a [search query](https://www.postman.com/youtrack-dev/workspace/youtrack/request/24356758-fa9a9439-98b5-463d-a954-9e235d5ed40d)
of YouTrack issues and some settings you can tune, and transform all issues into `VEvent` and export them to a `.ics`
file.

The app is written using Spring Boot, and heavily relies on Spring Boot Configuration to receive user input.

## Build

run `./gradlew assemble :yc-app-cli` at the root directory (not this current directory) to build the app.

Or run `./gradlew nativeCompile :yc-app-cli` at the root directory
(not this current directory) to build a native image if you have setup GraalVM with `native-image`.

## Usage

This is a CLI application, but all configs are done via Spring Boot Configuration.

So please be familiar with Spring
Boot's [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

Some good ways to use externalized configuration are Environment Variables,
Importing additional properties/YAML file, Passing command line, etc.

All configuration properties are prefixed with `cliapp.` and can be found
in [`application.yml`](src/main/resources/application.yml).

Unfortunately, I couldn't make the auto-generated configuration properties metadata work ☹,
so there is no documentation on `application.yml`.
Instead, documentation is written on `@ConfigurationProperties` classes, so you would need to look at source codes
in [`mikufan.cx.yc.cliapp.config` package](src/main/kotlin/mikufan/cx/yc/cliapp/config) to check the documentation 😔.

You can also check [`Acceptance1Test`](src/test/kotlin/mikufan/cx/yc/cliapp/MainAcceptanceTest.kt) for a sample usage.

## Advanced Usage with Cron + Nginx in Docker

It is possible that you can let Cron runs this CLI app to keep up-to-date with YouTrack,
and use Nginx in Docker to distribute the output .ics file publicly.

### Cron setup

First, build this application.

Then write a YAML file with all your preconfigured settings. You can just copy the `application.yml` file and modify it.

Then set up a CRON job to run the CLI app periodically.
Use [`spring.config.import`](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.files.importing)
command line argument to import your YAML file.

### Nginx setup (using Docker)

set the output file of this CLI app `cliapp.io.output-file` to some directory
that will be mounted into your nginx docker container.

This is a sample `docker-compose.yml` file that you can use to launch nginx with file listing enabled:

``` yaml
version: '3.9'

services:
  nginx:
    image: nginx:alpine
    container_name: nginx
    ports:
      - 80:80
      - 443:443
    volumes:
      - <dir with .ics file>:/public/files/
      - <dir with nginx config>:/etc/nginx/conf.d/
      - <dir with ssl cert>:/etc/ssl/
```

The `nginx.conf` file should look like this in the directory you mount to `/etc/nginx/conf.d/`:

``` properties
server {
    listen              443 ssl http2;
    listen              [::]:443 ssl http2;
    server_name         my.domain;

    # SSL
    ssl_certificate     /etc/ssl/my_ssl_certificate.crt;
    ssl_certificate_key /etc/ssl/my_ssl_certificate.key;

    # file listing
    root /public/files/;

    # to access the file at https://my.domain/sub/my-todo.ics
    location /sub/ {
        autoindex on;
    }
}

# HTTP redirect
server {
    listen      80;
    listen      [::]:80;
    server_name my.domain;
    return      301 https://my.domain$request_uri;
}
```
