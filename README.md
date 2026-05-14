# MyBatis 고객 문의 통계 쿼리 최적화 예제

## 프로젝트 목적

MyBatis 기반 통계 화면에서 흔히 발생하는 문제를 재현하고, 개선 전후를 직접 비교해볼 수 있도록 만든 프로젝트입니다.

통계 화면을 구현할 때 셀 단위로 COUNT 쿼리를 반복 실행하는 방식은 구현이 단순해 자주 선택되지만,
항목이 늘어날수록 DB 부하가 선형으로 증가하는 구조적인 문제를 안고 있습니다.
이 프로젝트는 그 문제를 가시화하고, 단계적으로 개선하는 과정을 코드로 보여줍니다.

## 예제 도메인

고객 문의 유형별 처리 상태를 집계하는 관리자 통계 화면을 단순화한 예제입니다.

기본 샘플 데이터는 약 3,200건이며, 애플리케이션 시작 시 `customer_inquiry` 테이블이 비어 있으면
Java 기반 초기화 코드가 MyBatis Mapper를 통해 자동으로 생성합니다. 모든 데이터는 공개 예제용 가상 데이터입니다.
카테고리와 상태는 완전히 균등하게 분포하지 않고, 통계 데이터에서 흔히 보이는 편중이 생기도록
고정 시드 기반 난수로 생성됩니다.

이 프로젝트는 실제 업무에서 통계 화면의 셀마다 COUNT 쿼리가 반복되어 약 240개에 가까운 COUNT 쿼리가 실행되던 구조를,
일반화된 고객 문의 처리 통계 예제로 재현하고 분석하기 위해 만들었습니다.
목표는 쿼리를 무조건 1개로 줄이는 것이 아니라, 여러 통계 처리 방식의 장단점을 비교하는 것입니다.

## v1. 셀 단위 COUNT 방식

각 통계 셀마다 개별 COUNT 쿼리를 실행하는 방식입니다.

행 8개 × 컬럼 8개 구조이므로 최대 64회의 COUNT 쿼리가 실행됩니다.
구현 자체는 단순하지만, 통계 항목이 늘어날수록 DB 조회 횟수가 함께 증가하는 구조적인 한계가 있습니다.

Controller의 `model.addAttribute` 호출을 의도적으로 반복 작성해, 문제의 구조가 코드에서 바로 드러나도록 했습니다.

### 상태 코드 안내

| 상태 코드 | 설명 |
|---|---|
| `TOTAL` | DB에 저장되는 값이 아닌 통계 계산용 가상 상태입니다. |
| `UNRESOLVED` | DB에 저장되는 값이 아닌 통계 계산용 가상 상태입니다. |

이 예제에서는 `RESOLVED`가 아닌 모든 문의를 미처리로 집계합니다.
실제 서비스에서는 미처리의 정의가 업무 정책에 따라 달라질 수 있습니다.

### 통계 수치 해석 방법

화면의 `미처리` 컬럼은 DB에 저장된 독립 상태가 아니라 통계 계산용 가상 상태입니다.
따라서 통계 숫자는 아래 관계로 해석해야 합니다.

```text
총계     = 접수 + 처리중 + 해결 + 보류 + 재문의 + 이관
미처리   = 총계 - 해결
미처리   = 접수 + 처리중 + 보류 + 재문의 + 이관
```

아래와 같은 해석은 잘못된 해석입니다.

```text
총계 = 접수 + 처리중 + 해결 + 보류 + 재문의 + 이관 + 미처리
```

`미처리`를 독립된 상태 컬럼으로 오해하면 합산 결과가 맞지 않는다고 느낄 수 있습니다.
v1에서는 실제 운영 환경에서 발생할 수 있는 통계 표현 혼동 사례를 그대로 남겨두었습니다.
이 문제는 셀 단위 COUNT 쿼리 반복 실행이라는 성능 문제와는 별개로,
UI 설계·용어 정의·요구사항 명세 단계에서 다뤄야 할 문제입니다.

## v2. 반복문 기반 리팩토링

v1과 동일하게 각 통계 셀마다 COUNT 쿼리를 실행합니다.
행 8개 × 컬럼 8개 구조이므로 쿼리 수는 최대 64회로 동일합니다.

이 단계에서 개선된 부분은 Controller의 반복적인 `model.addAttribute` 호출입니다.
통계 행과 열을 목록 기반으로 관리하고, 화면에는 `statTable` 하나만 전달합니다.
항목 추가나 수정이 필요할 때 변경 범위를 줄이는 것이 목적입니다.

v2는 성능 개선이 아니라 유지보수성 개선 단계입니다.

| 구분 | v1 | v2 |
|---|---|---|
| 쿼리 수 | 최대 64회 | 최대 64회 |
| Controller | 셀별 `model.addAttribute` 반복 | `statTable` 1개 전달 |
| 화면 출력 | 개별 attribute 출력 | 반복문 기반 출력 |
| 개선 목적 | 문제 재현 | 유지보수성 개선 |

## v3. 고정 컬럼 통계표를 위한 단일 집계 쿼리 방식

v3는 `SUM(CASE WHEN ...)`과 `GROUP BY`를 사용해 카테고리별 통계를 한 번에 조회합니다.
v1/v2는 최대 64회 COUNT 쿼리를 실행하지만, v3는 집계 쿼리 1회로 동일한 결과를 만듭니다.

이 방식은 현재 예제처럼 통계 컬럼이 고정된 화면에서 선택할 수 있는 방법입니다.
DB 왕복 횟수를 줄일 수 있는 대신, SQL이 길어지고 통계 규칙이 쿼리 안에 강하게 포함되며,
결과를 화면용 DTO로 조립하는 과정이 추가됩니다. 단일 집계 쿼리가 모든 통계 화면의 정답은 아니며,
통계 조건, 컬럼의 동적 여부, 데이터 크기, 인덱스 구성, 유지보수 기준에 따라 다른 방식이 더 적합할 수 있습니다.

| 구분 | v1 | v2 | v3 |
|---|---|---|---|
| 쿼리 수 | 최대 64회 | 최대 64회 | 1회 |
| Controller | 셀별 `model.addAttribute` 반복 | `statTable` 1개 전달 | `statTable` 1개 전달 |
| 화면 출력 | 개별 attribute 출력 | 반복문 기반 출력 | 반복문 기반 출력 |
| Mapper 호출 | 셀마다 count 호출 | 셀마다 count 호출 | 집계 쿼리 1회 |
| 개선 목적 | 문제 재현 | 유지보수성 개선 | DB 조회 횟수 개선 |
| 단점 | 중복 많음, 쿼리 많음 | 쿼리 수는 그대로 | SQL 복잡도 증가 |

## 통계 처리 방식은 상황에 따라 다르다

통계 쿼리 최적화의 목표는 쿼리 개수를 무조건 1개로 만드는 것이 아닙니다.
화면 요구사항, 통계 컬럼의 고정 여부, 데이터 양, 인덱스, 실시간성, 운영자가 이해할 수 있는 SQL인지에 따라 적절한 방식이 달라집니다.

| 방식 | 장점 | 단점/주의점 | 적합한 상황 |
|---|---|---|---|
| 셀 단위 COUNT 방식 | 구현이 단순하고 각 셀의 의미가 SQL에 직접 드러남 | 통계 셀이 늘어날수록 쿼리 수와 DB round-trip 증가 | 작은 관리자 화면, 셀 수가 적고 호출 빈도가 낮은 화면 |
| 반복문 기반 리팩토링 | 코드 중복 제거, 항목 추가/수정 범위 감소 | 쿼리 수는 그대로 | 기존 구조를 유지하면서 Controller/View 유지보수성을 먼저 개선할 때 |
| `SUM(CASE WHEN)` + `GROUP BY` 단일 집계 쿼리 | 고정된 통계 컬럼에서 DB round-trip 감소 | SQL 복잡도 증가, 통계 규칙이 SQL에 포함됨 | 컬럼과 상태가 비교적 고정된 통계표 |
| `GROUP BY category, status` 후 Java 피벗 | SQL이 단순하고 상태/분류 추가에 유연함 | Java에서 화면용 테이블 조립 필요 | 상태나 분류가 동적으로 늘어날 수 있는 통계 |
| 목적별 다중 집계 쿼리 | 단일 거대 SQL보다 읽기 쉽고 유지보수성이 좋을 수 있음 | 쿼리 수가 1개는 아니며 결과 조합 필요 | 240개 COUNT를 2~5개 목적별 집계 쿼리로 줄이는 절충안 |
| 요약 테이블 / 배치 집계 | 조회 성능이 좋고 대용량 반복 조회에 유리 | 실시간성, 정합성, 배치 관리 비용 발생 | 조회가 잦고 원천 데이터가 많은 통계 |
| 캐싱 | 동일 조건 반복 조회 시 DB 부하 감소 | 캐시 무효화와 최신성 문제 | 같은 조건의 통계가 자주 반복 조회되는 화면 |

이 프로젝트의 v1/v2/v3는 위 선택지 중 일부만 구현한 예제입니다.
`GROUP BY category, status` 후 Java 피벗, 목적별 다중 집계 쿼리, 요약 테이블, 캐싱은 추가 비교 후보로 문서에만 정리합니다.

## 실행 방법

기본 설정은 로컬 예제 실행용 샘플 DB 계정을 사용하며, 운영 환경과는 무관합니다.
다른 값을 사용해야 한다면 `.env.example`을 참고해 `.env` 파일을 만들거나,
아래 환경변수를 직접 지정하세요. `.env` 파일은 커밋 대상에 포함되지 않습니다.

| 환경변수 | 기본값 |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/stat_example?serverTimezone=Asia/Seoul&characterEncoding=UTF-8` |
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

- 로그에서 `SELECT COUNT(DISTINCT i.id)` 쿼리가 여러 번 실행되는지 확인합니다.
- v1과 v2 모두 최대 64회의 COUNT 쿼리가 실행됩니다.
- v3는 `SUM(CASE WHEN ...)` 기반 집계 쿼리 1회로 동일한 통계 결과를 만듭니다.

## 성능 테스트

v1/v2/v3의 성능 비교 목적은 특정 방식이 항상 빠르다는 결론을 내리는 것이 아니라,
쿼리 수, SQL 복잡도, DB round-trip, 데이터 수, 동시 요청 수가 어떤 차이를 만드는지 관찰하는 것입니다.

| 버전 | SQL 실행 횟수 | 설명 |
|---|---:|---|
| v1 | 최대 64회 | 셀 단위 COUNT 방식 |
| v2 | 최대 64회 | Controller 중복 제거, 쿼리 수는 동일 |
| v3 | 1회 | `SUM(CASE WHEN ...)` + `GROUP BY` 집계 쿼리 |

측정할 때는 다음 관점을 함께 봅니다.

- 셀 단위 COUNT 방식은 쿼리 수와 DB round-trip이 통계 셀 수에 비례합니다.
- v2는 코드 구조를 개선하지만 DB 접근 횟수는 줄이지 않습니다.
- v3는 DB 접근 횟수를 줄이지만 SQL 복잡도가 증가합니다.
- 데이터 수, 동시 요청 수, 인덱스 구성, 통계 조건에 따라 결과가 달라질 수 있습니다.

통계 화면 요청이 끝나면 애플리케이션 로그에 요청 시간과 SQL 집계 정보가 출력됩니다.

```text
[PERF] uri=/inquiries/stats/v1 elapsedMs=150 sqlCount=64 sqlTimeMs=90 thread=http-nio-8080-exec-1
[PERF] uri=/inquiries/stats/v3 elapsedMs=35 sqlCount=1 sqlTimeMs=20 thread=http-nio-8080-exec-2
```

SQL 실행 횟수와 SQL 총 실행 시간은 MyBatis Interceptor로 요청 단위 집계합니다. HikariCP 커넥션 풀 상태는 Actuator metrics로 확인합니다.

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

k6 스크립트는 `performance/k6/stat.js` 하나로 통합되어 있습니다.

`VERSION`, `VUS`, `DURATION`, `SLEEP`, `BASE_URL` 환경변수로 테스트 대상을 조정합니다.

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

예를 들어 더 강한 부하 조건은 아래처럼 실행할 수 있습니다.

```bash
VERSION=v1 VUS=50 DURATION=1m SLEEP=0 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=50 DURATION=1m SLEEP=0 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=50 DURATION=1m SLEEP=0 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

DB 실행 계획과 Performance Schema 확인 SQL은 `docs/sql` 아래에 있습니다. 테스트 계획은 `docs/performance-test-plan.md`, 성능 비교 결과는 `docs/performance-comparison.md`에 정리합니다.
