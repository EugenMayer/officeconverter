[![tests](https://github.com/EugenMayer/officeconverter/actions/workflows/tests.yml/badge.svg)](https://github.com/EugenMayer/officeconverter/actions/workflows/tests.yml)
[![docker publish](https://github.com/EugenMayer/officeconverter/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/EugenMayer/officeconverter/actions/workflows/docker-publish.yml)
## WAT

Offers a (i think production ready) REST service to convert files like PDF, docx,xlx .. odt .. you get it.. to other formats like pdf, png, doc, pdt, html.
This project is basically an extended version of [jodconverter-samples-rest](https://github.com/jodconverter/jodconverter-samples/tree/main/samples/spring-boot-rest)

You can use this project as it is using docker with `ghcr.io/eugenmayer/kontextwork-converter` or build it here yourself.

## Build

local java build, when you have all the build tools present + libreoffice locally installed

    ./gradlew build
    # you find the artifact in /tmp/gradle-officeconverter/builds/libs/officeconverter-*.jar
    # the jar file is a full tomcat bundled app, so just start it like that
    java -jar /tmp/gradle-officeconverter/build/libs/officeconverter-*.jar

or better use the docker image with everything included, no dev tools/LO needed locally

    # this builds the source a
    make build

    make start-prod

    # alternativly
    docker run --memory 512m --name converter-prod --rm -p 8080:8080 ghcr.io/eugenmayer/kontextwork-converter:production

You can now connect to the 5001 remote debugger port, just use the existing IntelliJ task if you like

## Tests

You can run the tests locally (you will need libreoffice installed)

```bash
./gradlew itTests
```

Or in the docker-container

```bash
make test
```

## Development

You can either use the IDE task or the local gradle

    ./gradlew -Pdev bootRun

Or even better, use the development container. You will not need any LibreOffice/Gradle installed locally

     make start-src # basically just docker-compose up

This fires up a docker container, mounts your source. To auto-rebuild and auto-restart he app very quick do this

     make watch
     # or just run ./watch.sh localy

## Debugging

Of course, you can just start using your IDE and debug that, but if you want to debug inside the docker container

    make start

And now connect(attach) to localhost 5001 for debugging `ghcr.io/eugenmayer/kontextwork-converter:development` has a default remote
debugging port enabled on 5001

## REST endpoints

Check the controller to understand the different endpoints

## Release

CI based on tags

## Configuration

You can configure the docker images by mounting `/etc/app/application.yml` and put whatever you like into them.

For example if you like to have 2 LibreOffice instances, you would put into the file

```yaml
# amount of libreOffice instances to start - one for each given port. So this means 2
jodconverter:
  local:
    port-numbers: 
      - 2002
      - 2003
    # change the tmp folder
    working-dir: /tmp
# change upload sizes
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB
# change the server port (where the REST app is listenting)
server:
    portL: 8090
```

### Adding addition document formats

You can edit the [src/resources/document-formats.json](src/resources/document-formats.json) and add new custom formats.
The original can be found at (https://github.com/jodconverter/jodconverter/blob/master/jodconverter-core/src/main/resources/document-formats.json)[jodconverter-core].

We already added support for dotx/xltx for example.

## Internals

- state of the art springboot 3.0 application exposing a classic rest service to convert office document
- using [jodconverter-spring-boot-starter](https://github.com/jodconverter/jodconverter/tree/master/jodconverter-spring-boot-starter) for wiring jodconverter-local services
- build on/for Java 17 for better Docker support

## Credits

A lot of credits go to jodconverter by [jodconverter](https://github.com/jodconverter/jodconverter) - we completely base on his work. Cheer him up!
