# ------------------------- builder
FROM openjdk:10-jdk as builder
RUN apt-get update \
  && apt-get -y install gradle \
  && mkdir /dist /src
COPY . /src
WORKDIR /src
# development build..with swagger and so on
RUN SPRING_PROFILES_ACTIVE=dev gradle clean build \
  && cp build/libs/*SNAPSHOT.war /dist/development.war

# production build
RUN SPRING_PROFILES_ACTIVE=prod gradle clean build \
  && cp build/libs/*SNAPSHOT.war /dist/production.war


# --------------------------- development image
FROM eugenmayer/jodconverter:base as development
ENV JAR_FILE_NAME=app.war
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
COPY --from=builder /dist/development.war ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
CMD ["java","-jar","/opt/converter/converter.jar"]


# --------------------------- production image
FROM development as production
COPY --from=builder /dist/production.war ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}