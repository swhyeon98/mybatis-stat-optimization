# Performance Test Plan

## 테스트 목적

v1, v2, v3의 응답 시간과 SQL 실행 구조를 비교한다. 목적은 v3가 항상 더 빠르다는 결론을 증명하는 것이 아니라, 통계 처리 방식별 트레이드오프를 관찰하는 것이다.

비교할 핵심 관점은 다음과 같다.

- v1은 셀 단위 COUNT 방식이므로 쿼리 수와 DB round-trip이 통계 셀 수에 비례한다.
- v2는 통계표의 행을 집계 단위로 보며, 8개 행 기준 최대 8회 SQL을 실행한다.
- v2는 실무에서 적용했던 개선 방식과 가장 유사한 버전이다.
- v3는 전체 통계표를 1회 집계 쿼리로 조회하지만, SQL 복잡도가 증가한다.
- 데이터 수, 동시 요청 수, 인덱스 구성, 통계 조건에 따라 결과가 달라질 수 있다.

## 비교 대상

| 버전 | 방식 | 예상 SQL 실행 수 |
|---|---|---:|
| v1 | 셀 단위 COUNT 방식 | 최대 64회 |
| v2 | 행 단위 집계 방식 | 최대 8회 |
| v3 | 전체 단일 집계 쿼리 방식 | 1회 |

## 데이터 크기별 테스트 계획

| 데이터 수 | 실행 예 |
|---:|---|
| 3,200 | 기본값 |
| 100,000 | `SAMPLE_DATA_SIZE=100000 ./gradlew bootRun` |
| 1,000,000 | `SAMPLE_DATA_SIZE=1000000 ./gradlew bootRun` |

데이터 크기를 바꿔 다시 생성하려면 기존 `customer_inquiry` 데이터를 비우거나 DB 볼륨을 초기화한다.

## 동시 요청 수별 테스트 계획

| 동시 사용자 | 목적 |
|---:|---|
| 1명 | 단일 요청 기준 성능 확인 |
| 10명 | 일반적인 병렬 요청 상황 확인 |
| 30명 | 커넥션 풀 대기와 응답 시간 증가 확인 |

## 측정 항목

- 평균 응답 시간
- p95 응답 시간
- SQL 실행 횟수
- SQL 총 실행 시간
- HikariCP active/idle/pending/max
- DB rows examined

## 테스트 전 주의사항

- 첫 요청은 warm-up으로 제외한다.
- 같은 조건에서 여러 번 반복 측정한다.
- 로컬 PC 상태에 따라 결과가 달라질 수 있다.
- 절대적인 숫자보다 v1/v2/v3의 상대 비교가 중요하다.
- v2 구현 방식이 행 단위 집계로 변경되었으므로, 기존 v2 측정 결과는 재측정이 필요하다.

## 실행 예

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

## 추가 비교 후보

이번 프로젝트에는 구현하지 않지만, 실제 통계 화면에서는 다음 방식도 비교 대상이 될 수 있다.

| 방식 | 검토 포인트 |
|---|---|
| `GROUP BY category, status` 후 Java 피벗 | SQL 단순성, 동적 상태/분류 대응, Java 조립 비용 |
| 목적별 다중 집계 쿼리 | 단일 거대 SQL 대신 2~5개 집계 쿼리로 나눌 때의 유지보수성 |
| 요약 테이블 / 배치 집계 | 조회 성능, 실시간성, 정합성, 배치 관리 비용 |
| 캐싱 | 반복 조회 감소 효과, 캐시 무효화, 최신성 |
