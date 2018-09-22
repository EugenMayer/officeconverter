## WAT

Offers a REST service to convert files like PDF, docx,xlx .. odt .. you get it.. to other formats like pdf, png, doc, pdt, html.
This project is basically an extended version of [jodconverter-sample-rest](https://github.com/sbraconnier/jodconverter/tree/master/jodconverter-samples/jodconverter-sample-rest)

You can use this project as is using docker with `eugenmayer/kontextwork-converter` or build it here yourself.

## build

    gradle build

## REST endpoints

Start the project and access `http://localhost:8080/swagger-ui.html` to browse, inspect and try the REST endpoints.
You find all the docs there.
 
## internals

 - state of the art springboot 2.x application exposing a classic rest service to convert office document
 - using [[jodconverter-spring-boot-starter](https://github.com/sbraconnier/jodconverter/tree/master/jodconverter-spring-boot-starter) for wiring jodconverter-local services
 - build on/for Java 10 for better Docker support
 
## Credits

A lot of credits go to jodconverter by [jodconverter](https://github.com/sbraconnier/jodconverter) - we completely base on his work. Cheer him up!