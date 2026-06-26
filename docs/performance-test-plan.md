# 성능 테스트 계획

## 1. 테스트 목적

고객 문의 통계 페이지 예제를 기준으로 통계 조회 방식에 따른 성능 차이를 비교한다.

비교 대상은 다음 세 가지 방식이다.

* v1: 셀 단위 COUNT 방식
* v2: 행 단위 집계 방식
* v3: 전체 단일 집계 쿼리 방식

성능 테스트에서는 각 방식의 요청당 SQL 실행 수, 서버 처리 시간, SQL 총 실행 시간, 동시 요청 상황에서의 지연 변화를 확인한다.

## 2. 비교 대상

| 버전 | 방식             | 요청당 SQL 실행 수 | 설명                                                 |
| -- | -------------- | -----------: | -------------------------------------------------- |
| v1 | 셀 단위 COUNT 방식  |          64회 | 8행 × 8개 수치 컬럼을 셀마다 개별 COUNT 쿼리로 조회                 |
| v2 | 행 단위 집계 방식     |           8회 | 한 행의 모든 수치를 한 번의 집계 쿼리로 조회                         |
| v3 | 전체 단일 집계 쿼리 방식 |           1회 | 전체 통계표를 `SUM(CASE WHEN ...)` + `GROUP BY`로 한 번에 조회 |

## 3. 테스트 대상 URL

| 버전 | URL                   |
| -- | --------------------- |
| v1 | `/inquiries/stats/v1` |
| v2 | `/inquiries/stats/v2` |
| v3 | `/inquiries/stats/v3` |

## 4. 테스트 데이터 조건

|    데이터 수 | 목적                           |
| -------: | ---------------------------- |
|   3,200건 | 기본 데이터 규모에서 v1, v2, v3 차이 확인 |
| 100,000건 | 데이터 증가 상황에서 v1, v2, v3 차이 확인 |

각 테스트는 동일한 데이터 조건에서 v1, v2, v3를 순서대로 측정한다.

## 5. 부하 조건

|   데이터 수 | VUS | Duration | Sleep | 목적                           |
| ------: | --: | -------- | ----- | ---------------------------- |
|   3,200 |   1 | 30s      | 1s    | 단일 사용자 기준 응답 시간 확인           |
|   3,200 |  30 | 60s      | 0.5s  | 동시 요청 상황에서 응답 시간 확인          |
| 100,000 |   1 | 30s      | 1s    | 데이터 증가 시 단일 사용자 기준 응답 시간 확인  |
| 100,000 |  30 | 60s      | 0.5s  | 데이터 증가 + 동시 요청 상황에서 응답 시간 확인 |

## 6. 측정 지표

| 지표                 | 설명                     |
| ------------------ | ---------------------- |
| 완료 요청 수            | 테스트 동안 정상 완료된 요청 수     |
| 요청당 SQL 수          | 페이지 1회 조회 시 실행된 SQL 수  |
| 총 SQL 수            | 완료 요청 수 × 요청당 SQL 수    |
| 서버 평균(ms)          | 애플리케이션 내부 처리 시간 평균     |
| 서버 p95(ms)         | 애플리케이션 내부 처리 시간 p95    |
| SQL 평균(ms)         | 요청 1회당 SQL 총 실행 시간 평균  |
| SQL p95(ms)        | 요청 1회당 SQL 총 실행 시간 p95 |
| Hikari pending max | 커넥션 풀 대기 요청 수의 최댓값     |
| 실패율                | 실패한 HTTP 요청 비율         |

## 7. 측정 전 확인 사항

* v1, v2, v3의 통계 결과가 동일한지 확인한다.
* 동일한 데이터 수 조건에서 각 버전을 측정한다.
* 테스트 전 애플리케이션과 DB 상태를 가능한 한 동일하게 맞춘다.
* k6 스크립트의 `VERSION`, `VUS`, `DURATION`, `SLEEP`, `BASE_URL` 값이 의도한 조건과 일치하는지 확인한다.
* Actuator에서 HikariCP metric이 조회되는지 확인한다.

## 8. 측정 명령

### 8.1 3,200건 / VUS 1

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

### 8.2 3,200건 / VUS 30

```bash
VERSION=v1 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

### 8.3 100,000건 / VUS 1

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

### 8.4 100,000건 / VUS 30

```bash
VERSION=v1 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=30 DURATION=60s SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

## 9. HikariCP metric 확인

테스트 중 또는 테스트 직후 HikariCP pending connection 값을 확인한다.

```bash
curl http://localhost:8080/actuator/metrics/hikaricp.connections.pending
```

필요하면 active connection도 함께 확인한다.

```bash
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```
