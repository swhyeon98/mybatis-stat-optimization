# MyBatis 고객 문의 통계 쿼리 최적화 예제

## 프로젝트 목적

MyBatis 기반 통계 화면에서 셀 단위 COUNT 쿼리가 반복 실행되는 구조를 재현하고, 여러 개선 방식의 장단점을 비교하기 위한 예제 프로젝트입니다.

실무에서 통계 페이지 응답 지연 문제를 분석했고, 셀 단위 COUNT 반복 호출 구조가 원인임을 확인했습니다. 실무에서는 화면 구조와 운영 안정성을 고려해 행 단위 집계 방식으로 개선하여 SQL 호출 수를 크게 줄였습니다. 이후 개인 프로젝트에서는 동일한 문제 구조를 단순화해 재현하고, 셀 단위 COUNT 방식, 행 단위 집계 방식, 전체 단일 집계 쿼리 방식을 비교했습니다.

이 프로젝트의 목표는 통계 쿼리를 무조건 1개로 줄이는 것이 아닙니다. 통계표 구조, 조건의 동적 여부, SQL 유지보수성, 데이터 크기, 인덱스 구성에 따라 어떤 방식이 적절한지 비교하는 것이 목적입니다.

## 예제 도메인

고객 문의 유형별 처리 상태를 집계하는 관리자 통계 화면을 단순화한 예제입니다.

기본 샘플 데이터는 약 3,200건이며, 애플리케이션 시작 시 `customer_inquiry` 테이블이 비어 있으면 Java 기반 초기화 코드가 MyBatis Mapper를 통해 자동으로 생성합니다. 모든 데이터는 공개 예제용 가상 데이터입니다.

카테고리와 상태는 완전히 균등하게 분포하지 않고, 통계 데이터에서 흔히 보이는 편중이 생기도록 고정 시드 기반 난수로 생성됩니다.

## v1. 셀 단위 COUNT 방식

통계표의 각 셀마다 개별 COUNT 쿼리를 실행하는 방식입니다.

- 통계 행: 전체, 결제/환불, 계정/로그인, 배송/예약, 오류/버그, 서비스 이용, 제휴/광고, 기타
- 통계 컬럼: 총계, 접수, 처리중, 해결, 보류, 재문의, 이관, 미처리
- SQL 실행 수: 8행 × 8개 수치 컬럼 = 최대 64회
- 구현 의도: 개선 전 문제 구조 재현

Controller의 `model.addAttribute` 호출을 의도적으로 반복 작성해, 문제의 구조가 코드에서 바로 드러나도록 했습니다.

## v2. 행 단위 집계 방식

통계표의 한 행을 하나의 집계 단위로 보고, 행마다 1개의 집계 쿼리를 실행하는 방식입니다.

한 번의 쿼리에서 해당 행의 총계, 접수, 처리중, 해결, 보류, 재문의, 이관, 미처리 값을 모두 조회합니다.

- SQL 실행 수: 8행 = 최대 8회
- 구현 방식: `SUM(CASE WHEN ...)` 기반 행 단위 집계 쿼리
- 개선 효과: 셀 단위 64회 호출을 행 단위 8회 호출로 축소
- 위치: 실무에서 적용했던 개선 방식과 가장 유사한 버전

v2는 전체 통계표를 1개의 거대한 SQL로 만들지 않고, 화면의 행 구조를 유지하면서 DB 왕복 횟수를 줄이는 절충안입니다.

## v3. 전체 단일 집계 쿼리 방식

`SUM(CASE WHEN ...)`과 `GROUP BY`를 사용해 전체 통계표를 1회 SQL로 조회하는 방식입니다.

v3는 실무 개선 경험을 바탕으로 개인 프로젝트에서 추가 확장 검증한 방식입니다. 실무에서 항상 v3처럼 1회 쿼리로 개선해야 한다는 의미가 아닙니다.

- SQL 실행 수: 1회
- 구현 방식: 카테고리별 집계 결과를 한 번에 조회하고, 전체 행은 Java Service에서 합산
- 장점: DB round-trip을 가장 크게 줄일 수 있음
- 단점: SQL이 길어지고 통계 규칙이 SQL에 강하게 포함됨

## 버전 비교

| 구분 | v1 | v2 | v3 |
|---|---|---|---|
| 방식 | 셀 단위 COUNT | 행 단위 집계 | 전체 단일 집계 |
| SQL 실행 수 | 최대 64회 | 최대 8회 | 1회 |
| Mapper 호출 | 셀마다 count 호출 | 행마다 집계 호출 | 전체 집계 1회 |
| Controller | 셀별 `model.addAttribute` 반복 | `statTable` 1개 전달 | `statTable` 1개 전달 |
| 화면 출력 | 개별 attribute 출력 | 반복문 기반 출력 | 반복문 기반 출력 |
| 개선 목적 | 문제 구조 재현 | 실무 개선 방식에 가까운 호출 수 축소 | 개인 프로젝트 추가 검증 |
| 주요 단점 | 중복 많음, 쿼리 많음 | 행 수만큼 쿼리 실행 | SQL 복잡도 증가 |

## 통계 수치 해석 방법

`TOTAL`과 `UNRESOLVED`는 DB에 저장되는 상태값이 아니라 통계 계산용 가상 상태입니다.

이 예제에서는 `RESOLVED`가 아닌 모든 문의를 미처리로 집계합니다. 실제 서비스에서는 미처리의 정의가 업무 정책에 따라 달라질 수 있습니다.

```text
총계     = 접수 + 처리중 + 해결 + 보류 + 재문의 + 이관
미처리   = 총계 - 해결
미처리   = 접수 + 처리중 + 보류 + 재문의 + 이관
```

아래와 같은 해석은 잘못된 해석입니다.

```text
총계 = 접수 + 처리중 + 해결 + 보류 + 재문의 + 이관 + 미처리
```

`미처리`를 독립된 상태 컬럼으로 오해하면 합산 결과가 맞지 않는다고 느낄 수 있습니다. 이 문제는 쿼리 성능 문제와는 별개의 UI/용어/요구사항 정의 문제입니다.

## 통계 처리 방식은 상황에 따라 다르다

통계 쿼리 최적화의 목표는 쿼리 개수를 무조건 1개로 만드는 것이 아닙니다. 화면 요구사항, 통계 컬럼의 고정 여부, 데이터 양, 인덱스, 실시간성, 운영자가 이해할 수 있는 SQL인지에 따라 적절한 방식이 달라집니다.

| 방식 | 장점 | 단점/주의점 | 적합한 상황 |
|---|---|---|---|
| 셀 단위 COUNT 방식 | 구현이 단순하고 각 셀의 의미가 SQL에 직접 드러남 | 통계 셀이 늘어날수록 쿼리 수와 DB round-trip 증가 | 작은 관리자 화면, 셀 수가 적고 호출 빈도가 낮은 화면 |
| 행 단위 집계 방식 | 화면 행 구조를 유지하면서 쿼리 수를 줄임 | 행 수만큼 SQL 실행, 행별 집계 SQL 필요 | 실무 화면 구조를 크게 바꾸지 않고 안정적으로 개선할 때 |
| `SUM(CASE WHEN)` + `GROUP BY` 단일 집계 쿼리 | 고정된 통계 컬럼에서 DB round-trip 감소 | SQL 복잡도 증가, 통계 규칙이 SQL에 포함됨 | 컬럼과 상태가 비교적 고정된 통계표 |
| `GROUP BY category, status` 후 Java 피벗 | SQL이 단순하고 상태/분류 추가에 유연함 | Java에서 화면용 테이블 조립 필요 | 상태나 분류가 동적으로 늘어날 수 있는 통계 |
| 목적별 다중 집계 쿼리 | 단일 거대 SQL보다 읽기 쉽고 유지보수성이 좋을 수 있음 | 쿼리 수가 1개는 아니며 결과 조합 필요 | 많은 COUNT를 2~5개 목적별 집계 쿼리로 줄이는 절충안 |
| 요약 테이블 / 배치 집계 | 조회 성능이 좋고 대용량 반복 조회에 유리 | 실시간성, 정합성, 배치 관리 비용 발생 | 조회가 잦고 원천 데이터가 많은 통계 |
| 캐싱 | 동일 조건 반복 조회 시 DB 부하 감소 | 캐시 무효화와 최신성 문제 | 같은 조건의 통계가 자주 반복 조회되는 화면 |

이 프로젝트의 공식 구현 버전은 v1, v2, v3 세 가지입니다. `GROUP BY category, status` 후 Java 피벗, 목적별 다중 집계 쿼리, 요약 테이블, 캐싱은 추가 비교 후보로 문서에만 정리합니다.

## 실행 방법

기본 설정은 로컬 예제 실행용 샘플 DB 계정을 사용하며, 운영 환경과는 무관합니다. 다른 값을 사용해야 한다면 `.env.example`을 참고해 `.env` 파일을 만들거나 환경변수를 직접 지정하세요. `.env` 파일은 커밋 대상에 포함되지 않습니다.

| 환경변수 | 기본값 |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3307/stat_example?serverTimezone=Asia/Seoul&characterEncoding=UTF-8` |
| `DB_USERNAME` | `stat_user` |
| `DB_PASSWORD` | `stat_pass` |
| `MYSQL_DATABASE` | `stat_example` |
| `MYSQL_ROOT_PASSWORD` | `root_pass` |
| `SAMPLE_DATA_ENABLED` | `true` |
| `SAMPLE_DATA_SIZE` | `3200` |

```bash
docker compose up -d
./gradlew bootRun
```

실행 후 브라우저에서 아래 주소로 접속합니다.

```text
http://localhost:8080/inquiries/stats/v1
http://localhost:8080/inquiries/stats/v2
http://localhost:8080/inquiries/stats/v3
```

## 확인 포인트

- v1은 `SELECT COUNT(DISTINCT i.id)`가 최대 64회 실행됩니다.
- v2는 행 단위 집계 쿼리가 최대 8회 실행됩니다.
- v3는 `SUM(CASE WHEN ...)`과 `GROUP BY` 기반 전체 집계 쿼리 1회로 동일한 통계 결과를 만듭니다.
- v1/v2/v3의 화면 숫자는 동일해야 합니다.

## 성능 테스트

v1/v2/v3의 성능 비교 목적은 특정 방식이 항상 빠르다는 결론을 내리는 것이 아니라, 쿼리 수, SQL 복잡도, DB round-trip, 데이터 수, 동시 요청 수가 어떤 차이를 만드는지 관찰하는 것입니다.

| 버전 | SQL 실행 횟수 | 설명 |
|---|---:|---|
| v1 | 최대 64회 | 셀 단위 COUNT 방식 |
| v2 | 최대 8회 | 행 단위 집계 방식 |
| v3 | 1회 | 전체 단일 집계 쿼리 방식 |

통계 화면 요청이 끝나면 응답 헤더에 요청 시간과 SQL 집계 정보가 포함됩니다.

```text
X-Perf-Elapsed-Ms: 150
X-Perf-Sql-Count: 64
X-Perf-Sql-Time-Ms: 90
```

`app.performance.log-enabled=true`를 설정하면 애플리케이션 로그에도 같은 정보를 출력할 수 있습니다.

```text
[PERF] uri=/inquiries/stats/v1 elapsedMs=150 sqlCount=64 sqlTimeMs=90 thread=http-nio-8080-exec-1
[PERF] uri=/inquiries/stats/v2 elapsedMs=80 sqlCount=8 sqlTimeMs=45 thread=http-nio-8080-exec-2
[PERF] uri=/inquiries/stats/v3 elapsedMs=35 sqlCount=1 sqlTimeMs=20 thread=http-nio-8080-exec-3
```

HikariCP 커넥션 풀 상태는 Actuator metrics로 확인합니다.

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/metrics/hikaricp.connections.active
http://localhost:8080/actuator/metrics/hikaricp.connections.idle
http://localhost:8080/actuator/metrics/hikaricp.connections.pending
http://localhost:8080/actuator/metrics/hikaricp.connections.max
http://localhost:8080/actuator/metrics/hikaricp.connections.acquire
http://localhost:8080/actuator/metrics/hikaricp.connections.usage
```

데이터 크기는 `SAMPLE_DATA_SIZE`로 조정할 수 있습니다. 기존 데이터가 있으면 샘플 데이터는 중복 생성되지 않으므로, 데이터 크기를 바꿔 다시 측정하려면 `customer_inquiry` 데이터를 비우거나 Docker 볼륨을 초기화해야 합니다.

```bash
SAMPLE_DATA_SIZE=100000 ./gradlew bootRun
SAMPLE_DATA_SIZE=1000000 ./gradlew bootRun
```

k6 스크립트는 `performance/k6/stat.js` 하나로 통합되어 있습니다. v2 구현 방식이 변경되었으므로 기존 측정 결과가 있다면 재측정이 필요합니다.

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

더 강한 부하 조건은 아래처럼 실행할 수 있습니다.

```bash
VERSION=v1 VUS=30 DURATION=1m SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=30 DURATION=1m SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=30 DURATION=1m SLEEP=0.5 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

성능 비교 계획은 `docs/performance-test-plan.md`, 버전 비교 요약과 재측정 메모는 `docs/performance-comparison.md`를 사용합니다. DB 실행 계획과 Performance Schema 확인 SQL은 `docs/sql` 아래에 있습니다.
