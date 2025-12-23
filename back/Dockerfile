FROM gradle:jdk-25-and-25 AS builder

WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN gradle dependencies --no-daemon

COPY .env .
COPY src src

RUN gradle build --no-daemon

FROM container-registry.oracle.com/graalvm/jdk:25

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/.env .env

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]