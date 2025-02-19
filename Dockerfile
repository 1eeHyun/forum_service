# Build
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test

# Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR
COPY --from=build /app/build/libs/*.jar app.jar

CMD java -jar /app/app.jar
