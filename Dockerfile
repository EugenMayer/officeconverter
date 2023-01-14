ARG BASE_IMAGE_VERSION=0.0.1
# ------------------------- builder
FROM bellsoft/liberica-openjdk-debian:17 as builder
RUN mkdir -p /src
COPY . /src
WORKDIR /src

ARG VERSION=0.0.1-snapshot

RUN  ./gradlew --no-daemon -Pversion=$VERSION clean build \
  && mkdir -p /dist && cp /src/build/libs/officeconverter-$VERSION.jar /dist/production.jar

# --------------------------- appbase image
FROM ghcr.io/jodconverter/jodconverter-runtime:$BASE_IMAGE_VERSION as appbase
COPY ./bin/docker-entrypoint.sh /docker-entrypoint.sh

RUN mkdir -p ${JAR_FILE_BASEDIR} /etc/app \
  && touch /etc/app/application.properties /var/log/app.log /var/log/app.err \
  && chmod +x /docker-entrypoint.sh \
  && chown $NONPRIVUSER:$NONPRIVGROUP /var/log/app.log /var/log/app.err

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["--spring.config.additional-location=optional:/etc/app/"]

# --------------------------- production image
FROM appbase as production
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
