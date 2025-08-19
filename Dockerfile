# -----------------------------------------------------------------------------
# Multi-stage Dockerfile for Spring Boot (Java 17, Gradle) application
# 목적: 일관된 개발/실행 환경을 위한 경량 이미지 생성
# 단계 1(builder): Gradle + JDK로 빌드
# 단계 2(runtime): JRE 슬림 이미지에 빌드 산출물(JAR)만 탑재
# -----------------------------------------------------------------------------

# 1) Builder stage: Gradle + JDK 환경에서 애플리케이션 빌드
FROM gradle:8.7-jdk17 AS builder

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /home/gradle/src

# (권장) .dockerignore로 build/, .gradle/ 등 불필요한 파일 제외를 추천합니다.
# 전체 프로젝트를 복사해 빌드(개발 편의성 우선)
COPY --chown=gradle:gradle . /home/gradle/src

# 캐시 효율을 위해 --no-daemon 사용. 테스트는 개발 편의상 제외(-x test)
RUN gradle clean build -x test --no-daemon

# 빌드 결과물에서 실행 가능한 Boot JAR 하나를 표준 경로로 복사
# - 일반적으로 build/libs/*.jar 생성됨
# - "-plain" JAR가 있을 수 있어 이를 제외하고 선택
RUN bash -lc 'set -euo pipefail; \
    JAR_PATH="$(ls build/libs/*.jar | grep -v "-plain" | head -n 1 || true)"; \
    if [ -z "$JAR_PATH" ]; then \
      # fallback: plain 또는 어떤 것이든 첫 번째 jar 사용
      JAR_PATH="$(ls build/libs/*.jar | head -n 1)"; \
    fi; \
    cp "$JAR_PATH" /home/gradle/app.jar'


# 2) Runtime stage: 경량 JRE 이미지에 JAR만 탑재
# 참고: openjdk:17-jre-slim 이미지가 레지스트리에서 제공되지 않는 환경이 있어
#      안정적으로 사용 가능한 eclipse-temurin 기반 이미지를 사용합니다.
FROM eclipse-temurin:17-jre AS runtime

# 한국 시간대 및 UTF-8 설정(선택)
ENV TZ=Asia/Seoul \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# 애플리케이션 실행 디렉토리
WORKDIR /app

# 빌더에서 생성한 실행 JAR만 복사 → 최종 이미지 최소화
COPY --from=builder /home/gradle/app.jar /app/app.jar

# 애플리케이션 포트(스프링 기본 8080)
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]


