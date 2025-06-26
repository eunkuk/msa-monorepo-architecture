# RUNNING.md

## 실행 및 환경 구성 가이드

이 프로젝트는 멀티 모듈(Monorepo) 구조로 관리되며,  
Kafka, Eureka, 각 마이크로서비스가 순차적으로 실행되어야 합니다.  
아래 안내를 따라 차례대로 실행해주세요.

---

### 1. Kafka 실행 (가장 먼저 실행)

#### 1) 볼륨 디렉토리 생성

    mkdir -p volumes/zookeeper/data volumes/zookeeper/logs volumes/kafka1/data volumes/kafka2/data

#### 2) Docker Compose로 Kafka 클러스터 및 모니터링 도구 실행

    docker-compose -f kafka-compose.yml up -d

- **포트**
  - Kafka: 19092, 19093
  - Zookeeper: 2181
  - Kafdrop: 9000 (http://localhost:9000)
  - Kafka UI: 8080 (http://localhost:8080)

---

### 2. Eureka Server 실행 (두 번째)

    cd eureka_server
    ./gradlew bootRun

- **관리 UI:** http://localhost:8761

---

### 3. Audio Server 실행 (세 번째)

    cd audio_server
    ./gradlew bootRun

- **API 엔드포인트:**
  - /record/start
  - /record/idle
  - /record/end
  - /api/files
  - /api/download/{fileName}
- **포트:** 5001

---

### 4. API Gateway 실행 (마지막)

    cd api_gateway
    ./gradlew bootRun

- **포트:** 5000

---

## 실행 순서가 중요한 이유

1. **Kafka**는 이벤트 메시징 처리를 위해 가장 먼저 구동해야 합니다.
2. **Eureka Server**는 서비스 디스커버리를 제공하므로 반드시 Audio Server, API Gateway보다 먼저 실행합니다.
3. **Audio Server**는 Eureka에 먼저 등록되어야 하므로 Eureka 실행 후 기동해야 합니다.
4. **API Gateway**는 모든 서비스가 준비된 후 마지막에 실행하는 것이 가장 안정적입니다.

---

## 서비스 확인

- **Eureka Dashboard**: http://localhost:8761
- **Kafka 모니터링(Kafdrop)**: http://localhost:9000
- **Kafka UI**: http://localhost:8080
- **Audio Server**: http://localhost:5001
- **API Gateway**: http://localhost:5000

---

## 빌드 방법

이 프로젝트는 멀티 모듈 프로젝트이므로  
**반드시 루트 디렉토리에서 빌드**해야 합니다.

    cd 프로젝트_루트_경로
    ./gradlew build

> **주의**: 개별 모듈에서 직접 빌드할 경우 의존성 문제가 발생할 수 있으니  
> 항상 루트 디렉토리에서 빌드하세요.

---
