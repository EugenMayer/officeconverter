## WAT

IT'S WIP - yet not finished. This README does promise more then actually is finished :)

Offers a REST service to convert files like PDF, docx,xlx .. odt .. you get it.. to other formats like pdf, png, doc, pdt, html.
This project is basically an extended version of [jodconverter-sample-rest](https://github.com/sbraconnier/jodconverter/tree/master/jodconverter-samples/jodconverter-sample-rest)

You can use this project as is using docker with `eugenmayer/kontextwork-converter` or build it here yourself.

## build

local java build, when you have all the build tools present + libreoffice locally installed

    ./gradlew build
    # you find the artifact in builds/libs/*-SNAPSHOT.war
    # the war file is a full tomcat bundled app, so just start it like that
    java -jar build/libs/converter-0.0.1-SNAPSHOT.war


or better use the docker image with all included, no dev tools/LO needed locally
    
    # this builds the source a
    make build

    make start-prod # or `make start` for the variant with swagger
    
    # alternativly
    docker run --memory 512m --name converter-prod --rm -p 8080:8080 eugenmayer/converter:production
    # or dev mode with swagger
    docker run --memory 512m --name converter-dev --rm -p 8080:8080 eugenmayer/converter:development
    
## REST endpoints

Start the project and access `http://localhost:8080/swagger-ui.html` to browse, inspect and try the REST endpoints.
You find all the docs there.
 
## internals

 - state of the art springboot 2.x application exposing a classic rest service to convert office document
 - using [[jodconverter-spring-boot-starter](https://github.com/sbraconnier/jodconverter/tree/master/jodconverter-spring-boot-starter) for wiring jodconverter-local services
 - build on/for Java 10 for better Docker support
 
## Credits

A lot of credits go to jodconverter by [jodconverter](https://github.com/sbraconnier/jodconverter) - we completely base on his work. Cheer him up!