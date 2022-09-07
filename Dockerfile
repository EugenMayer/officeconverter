ARG VERSION=0.0.1-snapshot
ARG JODCONVERTER_VERSION=4.4.2-2.0
# ------------------------- builder
FROM bellsoft/liberica-openjdk-debian:17 as builder
RUN mkdir -p /src
COPY . /src
WORKDIR /src

RUN  ./gradlew --no-daemon -Pversion=$VERSION clean build \
  && mkdir -p /dist && cp /src/build/libs/officeconverter-$VERSION.jar /dist/production.jar


# --------------------------- production image
FROM ghcr.io/eugenmayer/jodconverter:base-$JODCONVERTER_VERSION as production
ENV JAR_FILE_NAME=officeconverter.jar
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
COPY --from=builder /dist/production.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
EXPOSE 8080

# --------------------------- development image
FROM production as development
COPY --from=builder /dist/production.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
EXPOSE 8080
EXPOSE 5001
CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001", "-jar","/opt/app/officeconverter.jar"]

# just a trick to ensure that he default docker image resulting is production
FROM production
