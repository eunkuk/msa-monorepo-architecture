# 마이크로서비스 아키텍처 프로젝트

이 프로젝트는 Spring Cloud 기반의 MonoRepo 마이크로서비스 아키텍처를 구현한 예제입니다. 주요 구성 요소와 실행 순서에 대한 설명입니다.

## 구성 요소

### 1. Kafka (kafka-compose.yml)
- **역할**: 메시지 브로커로서 서비스 간 비동기 통신을 담당
- **기능**: 이벤트 기반 통신, 데이터 스트리밍, 실시간 메시지 처리
- **구성**: 
  - Zookeeper: Kafka 클러스터 관리
  - Kafka 브로커 2개: 메시지 저장 및 처리
  - Kafdrop: Kafka 모니터링 UI (http://localhost:9000)
  - Kafka UI: 고급 관리 도구 (http://localhost:8080)
- **포트**: 19092, 19093 (Kafka), 2181 (Zookeeper)

### 2. Eureka Server
- **역할**: 서비스 디스커버리 및 레지스트리 서버
- **기능**: 서비스 등록, 검색, 헬스 체크, 상태 관리
- **특징**: 서비스 인스턴스의 동적 확장 및 축소 지원
- **포트**: 8761 (관리 UI: http://localhost:8761)

### 3. Audio Server
- **역할**: 오디오 녹음 및 파일 관리를 담당하는 마이크로서비스
- **기능**: 
  - 오디오 녹음 세션 관리 (/record/start, /record/idle, /record/end)
  - 녹음된 오디오 파일 목록 조회 (/api/files)
  - 녹음된 오디오 파일 다운로드 (/api/download/{fileName})
- **특징**: WebM 형식으로 오디오 파일 저장, Base64 인코딩 사용
- **포트**: 5001

### 4. API Gateway
- **역할**: 클라이언트 요청을 적절한 서비스로 라우팅하는 중앙 관문
- **기능**: 
  - 요청 라우팅 및 로드 밸런싱
  - WebSocket 연결 처리
- **포트**: 5000

## 공통 모듈

### 1. core-common
- **역할**: 여러 모듈에서 공통으로 사용되는 유틸리티 클래스 (String, Date, File 등)를 제공합니다.
- **특징**: 특정 프레임워크에 대한 의존성 없이 순수 Java 코드로 작성되어 어떤 프로젝트에서도 재사용 가능합니다.

### 2. core-web
- **역할**: Spring Web 환경에 특화된 공통 기능을 제공합니다.
- **기능**:
  - `WebClient` 빈을 자동 설정하여 다른 서비스에서 HTTP 통신을 쉽게 할 수 있도록 지원합니다.
  - AOP 기반의 로깅, 예외 처리, 보안 필터 등 웹 애플리케이션에 필요한 공통 기능을 포함합니다.
- **특징**: `spring-boot-autoconfigure`를 통해 별도 설정 없이 의존성 추가만으로 기능을 활성화할 수 있습니다.

## 빌드 방법

이 프로젝트는 멀티 모듈 프로젝트이므로 반드시 루트 디렉토리에서 빌드해야 합니다.

```bash
# 프로젝트 루트 디렉토리로 이동
cd project-root-path

# Gradle로 빌드
./gradlew build
```

> **주의**: 개별 모듈 디렉토리에서 직접 빌드하면 의존성 문제가 발생할 수 있습니다. 항상 프로젝트 루트 디렉토리에서 빌드하세요.

## 실행 순서 및 방법

### 1. Kafka 실행 (첫 번째로 실행)
```bash
# 필요한 볼륨 디렉토리 생성
mkdir -p volumes/zookeeper/data volumes/zookeeper/logs volumes/kafka1/data volumes/kafka2/data

# Docker Compose로 Kafka 실행
docker-compose -f kafka-compose.yml up -d
```

### 2. Eureka Server 실행 (두 번째로 실행)
```bash
# Eureka Server 디렉토리로 이동
cd eureka_server

# Gradle로 실행
./gradlew bootRun
```

### 3. Audio Server 실행 (세 번째로 실행)
```bash
# Audio Server 디렉토리로 이동
cd audio_server

# Gradle로 실행
./gradlew bootRun
```

### 4. API Gateway 실행 (마지막으로 실행)
```bash
# API Gateway 디렉토리로 이동
cd api_gateway

# Gradle로 실행
./gradlew bootRun
```

## 실행 순서가 중요한 이유

1. **Kafka**는 독립적으로 실행되며 API Gateway의 메시지 처리 기능에 필요합니다.
2. **Eureka Server**는 서비스 디스커버리를 담당하므로 다른 서비스보다 먼저 실행되어야 합니다.
3. **Audio Server**는 Eureka Server에 자신을 등록하므로 Eureka Server가 실행된 후에 시작해야 합니다.
4. **API Gateway**는 Eureka Server에 자신을 등록하고 Audio Server로 요청을 라우팅하므로 다른 서비스가 준비된 후 마지막에 실행하는 것이 좋습니다.

## 서비스 확인 방법

- **Eureka Dashboard**: http://localhost:8761
- **Kafka 모니터링(Kafdrop)**: http://localhost:9000
- **Kafka UI**: http://localhost:8080
- **Audio Server**: http://localhost:5001
- **API Gateway**: http://localhost:5000
