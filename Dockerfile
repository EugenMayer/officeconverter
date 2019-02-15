# ------------------------- builder
FROM openjdk:11-jdk as builder
RUN apt-get update \
  && mkdir /dist /src
COPY . /src
WORKDIR /src
# HINT: yet here is no difference in the build of dev / prod. We just use different startup commands later in the docker images
# development build..with swagger and so on
RUN SPRING_PROFILES_ACTIVE=dev ./gradlew --no-daemon clean build \
  && cp build/libs/*SNAPSHOT.war /dist/development.war

# production build
RUN SPRING_PROFILES_ACTIVE=prod ./gradlew --no-daemon clean build \
  && cp build/libs/*SNAPSHOT.war /dist/production.war


# --------------------------- production image
FROM eugenmayer/jodconverter:base as production
ENV JAR_FILE_NAME=officeconverter.war
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
COPY --from=builder /dist/production.war ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
EXPOSE 8080

# --------------------------- development image
FROM production as development
COPY --from=builder /dist/development.war ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
EXPOSE 8080
EXPOSE 5001
CMD ["java","-Dspring.profiles.active=dev","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001", "-jar","/opt/app/officeconverter.war"]

# just a trick to ensure that he default docker image resulting is production
FROM production
