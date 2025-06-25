# Monorepo 환경에서 Docker로 MSA 관리하기

이 문서는 Gradle 멀티 모듈 프로젝트(Monorepo) 환경에서 Docker와 Docker Compose를 사용하여 마이크로서비스를 효율적으로 빌드하고 관리하는 방법을 설명합니다.

## 1. 멀티 스테이지 Dockerfile 작성

프로젝트 루트에 단일 `Dockerfile`을 생성하여 모든 서비스를 빌드하고 각 서비스별로 실행 이미지를 만듭니다. 이 방식은 빌드 캐시를 활용하고 이미지 크기를 최적화하는 데 유리합니다.

### Dockerfile 예시

```Dockerfile
# =================================
# 1. 빌드 스테이지 (Builder)
# =================================
# Gradle과 JDK 11을 포함한 이미지를 기반으로 빌더 환경을 구성합니다.
FROM gradle:7.4-jdk11 AS builder

# 소스 코드를 컨테이너의 /app 디렉토리로 복사합니다.
COPY . /app
WORKDIR /app

# Gradle을 사용하여 전체 프로젝트를 빌드합니다.
# 이 명령은 모든 하위 모듈(api_gateway, audio_server 등)을 빌드하고 JAR 파일을 생성합니다.
RUN ./gradlew build -x test

# =================================
# 2. 실행 스테이지 (Runner)
# =================================
# 각 서비스에 대한 실행 이미지를 별도로 정의합니다.
# JRE만 포함된 가벼운 이미지를 사용하여 최종 이미지 크기를 줄입니다.

# 2.1. Eureka Server 실행 이미지
FROM openjdk:11-jre-slim
WORKDIR /app
# 빌더 스테이지에서 생성된 Eureka Server의 JAR 파일을 복사합니다.
COPY --from=builder /app/eureka_server/build/libs/*.jar ./eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]

# 2.2. Audio Server 실행 이미지
FROM openjdk:11-jre-slim
WORKDIR /app
# 빌더 스테이지에서 생성된 Audio Server의 JAR 파일을 복사합니다.
# 의존하는 core-common, core-web 모듈은 이미 JAR 파일에 포함되어 있습니다.
COPY --from=builder /app/audio_server/build/libs/*.jar ./audio-server.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "audio-server.jar"]

# 2.3. API Gateway 실행 이미지
FROM openjdk:11-jre-slim
WORKDIR /app
# 빌더 스테이지에서 생성된 API Gateway의 JAR 파일을 복사합니다.
COPY --from=builder /app/api_gateway/build/libs/*.jar ./api-gateway.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
```

> **참고**: `build.gradle`에 `bootJar` 태스크가 활성화되어 있어야 실행 가능한 JAR 파일이 생성됩니다. Spring Boot 프로젝트는 기본적으로 활성화되어 있습니다.

## 2. Docker Compose 설정

프로젝트 루트에 `docker-compose.yml` 파일을 작성하여 위에서 정의한 Dockerfile을 기반으로 각 서비스를 컨테이너로 실행합니다.

### docker-compose.yml 예시

```yaml
version: '3.8'

services:
  eureka-server:
    build:
      context: .
      dockerfile: Dockerfile
      target: openjdk:11-jre-slim  # Eureka Server 실행 이미지를 타겟으로 지정
    container_name: eureka-server
    ports:
      - "8761:8761"

  audio-server:
    build:
      context: .
      dockerfile: Dockerfile
      target: openjdk:11-jre-slim # Audio Server 실행 이미지를 타겟으로 지정
    container_name: audio-server
    ports:
      - "5001:5001"
    depends_on:
      - eureka-server
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  api-gateway:
    build:
      context: .
      dockerfile: Dockerfile
      target: openjdk:11-jre-slim # API Gateway 실행 이미지를 타겟으로 지정
    container_name: api-gateway
    ports:
      - "5000:5000"
    depends_on:
      - eureka-server
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

```

> **주의**: 위 `docker-compose.yml`은 Kafka를 제외한 애플리케이션 서비스만 실행하는 예시입니다. Kafka를 함께 실행하려면 `kafka-compose.yml`의 내용을 참고하여 통합해야 합니다.

## 3. Docker Compose 실행

아래 명령어로 모든 서비스를 한 번에 빌드하고 실행할 수 있습니다.

```bash
# --build 옵션으로 이미지를 새로 빌드하고 컨테이너를 실행합니다.
docker-compose up --build
```
