# syntax=docker/dockerfile:1

# --- build stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 의존성 캐시 레이어: 빌드 스크립트·래퍼 먼저 복사
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# --- run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
