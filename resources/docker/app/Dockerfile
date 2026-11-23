FROM eclipse-temurin:26-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle VERSION ./
RUN chmod +x gradlew \
    && ./gradlew dependencies --configuration runtimeClasspath --no-daemon --quiet > /dev/null

COPY src src
COPY resources/oas resources/oas
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:26-jre-alpine

RUN addgroup -S app && adduser -S -G app -h /app app
WORKDIR /app

COPY --from=build --chown=app:app /workspace/build/libs/dummy-smtp-server-*.jar app.jar

USER app
EXPOSE 9086 6969

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
