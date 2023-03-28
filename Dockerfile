#
# Build
#
FROM maven:3.8.4-jdk-11-slim as buildtime
WORKDIR /build
COPY . .
RUN ls # Aggiunto per visualizzare i file nella cartella /build
RUN mvn clean package
RUN ls target/ # Aggiunto per visualizzare i file nella cartella /build/target

FROM adoptopenjdk/openjdk11:alpine-jre as builder
COPY --from=buildtime /build/target/*.jar application.jar
RUN ls # Aggiunto per visualizzare i file nella cartella corrente, inclusa application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ghcr.io/pagopa/docker-base-springboot-openjdk11:v1.0.0
COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./
RUN ls # Aggiunto per visualizzare i file nella cartella corrente dopo aver copiato tutti i layer dell'applicazione

EXPOSE 8080
