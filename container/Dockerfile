################################################################################
# Build stage 0: builder
################################################################################
FROM maven:3-openjdk-17 AS builder

WORKDIR /usr/src/app

COPY .. .

RUN mvn clean package spring-boot:repackage --batch-mode -DskipTests -DskipITs; \
    mv /usr/src/app/campsite-booking-service/target/campsite-booking-service-$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout).jar \
        /usr/src/app/app.jar

################################################################################
# Build stage 1: actual campsite-booking api image
################################################################################
FROM azul/zulu-openjdk-debian:17-jre-latest

LABEL \
    maintainer="Igor Baiborodine <igor@kiroule.com>" \
    org.label-schema.schema-version="1.0" \
    org.label-schema.name="campsite-booking" \
    org.label-schema.vcs-url="https://github.com/igor-baiborodine/campsite-booking" \
    org.label-schema.usage="https://github.com/igor-baiborodine/campsite-booking/blob/master/README.md"

ENV APP_HOME /opt/campsite
ENV APP_USER campsite
ENV APP_GROUP campsite

RUN groupadd ${APP_GROUP}; \
    useradd -g ${APP_GROUP} ${APP_USER}

RUN set -ex; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        # su tool for easy step-down from root
        gosu; \
    rm -rf /var/lib/apt/lists/*; \
    gosu nobody true

COPY --from=builder --chown=${APP_USER}:${APP_GROUP} /usr/src/app/app.jar ${APP_HOME}/app.jar
COPY container/docker-entrypoint.sh /usr/local/bin/

RUN chmod a+x /usr/local/bin/docker-entrypoint.sh

WORKDIR ${APP_HOME}
ENTRYPOINT ["docker-entrypoint.sh"]
EXPOSE 8080
CMD ["bash", "-c", "java -jar $APP_HOME/app.jar"]
