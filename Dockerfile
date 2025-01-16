ARG RUNTIME_VERSION

# ------------------------- builder
FROM bellsoft/liberica-openjdk-debian:17 as builder
ARG VERSION=0.0.1-snapshot

RUN mkdir -p /src
COPY . /src
WORKDIR /src

RUN  ./gradlew --no-daemon -Pversion=$VERSION clean build \
  && mkdir -p /dist && cp /src/build/libs/officeconverter-$VERSION.jar /dist/production.jar

# --------------------------- production image
ARG RUNTIME_VERSION
FROM ghcr.io/jodconverter/jodconverter-runtime:$RUNTIME_VERSION as production

######## COMMON BLOCK - PLEASE SYNC WITH DEV IMAGE ############
ENV JAR_FILE_NAME=officeconverter.jar
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY ./bin/docker-entrypoint.sh /docker-entrypoint.sh
RUN mkdir -p ${JAR_FILE_BASEDIR} /etc/app \
  && touch /etc/app/application.properties /var/log/app.log /var/log/app.err \
  && chmod +x /docker-entrypoint.sh \
  && chown $NONPRIVUSER:$NONPRIVGROUP /var/log/app.log /var/log/app.err

COPY --from=builder /dist/production.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}

ENTRYPOINT ["/docker-entrypoint.sh"]
EXPOSE 8080
######## / COMMON BLOCK - PLEASE SYNC WITH DEV IMAGE / ############
CMD ["--spring.config.additional-location=optional:/etc/app/"]

# --------------------------- development image
ARG RUNTIME_VERSION
FROM ghcr.io/jodconverter/jodconverter-runtime:jdk-$RUNTIME_VERSION as development

######## COMMON BLOCK - PLEASE SYNC WITH PROD IMAGE ############
ENV JAR_FILE_NAME=officeconverter.jar
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY ./bin/docker-entrypoint.sh /docker-entrypoint.sh
RUN mkdir -p ${JAR_FILE_BASEDIR} /etc/app \
  && touch /etc/app/application.properties /var/log/app.log /var/log/app.err \
  && chmod +x /docker-entrypoint.sh \
  && chown $NONPRIVUSER:$NONPRIVGROUP /var/log/app.log /var/log/app.err

COPY --from=builder /dist/production.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}

ENTRYPOINT ["/docker-entrypoint.sh"]
EXPOSE 8080
######## / COMMON BLOCK - PLEASE SYNC WITH PROD IMAGE / ############

EXPOSE 5001
CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001", "-jar","/opt/app/officeconverter.jar"]

# just a trick to ensure that he default docker image resulting is production
FROM production
