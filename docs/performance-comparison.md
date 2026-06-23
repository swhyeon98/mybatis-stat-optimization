# Performance Comparison

## 비교 흐름

| 버전 | 방식 | SQL 실행 수 | 설명 |
|---|---|---:|---|
| v1 | 셀 단위 COUNT 방식 | 최대 64회 | 8행 × 8개 수치 컬럼을 셀마다 조회 |
| v2 | 행 단위 집계 방식 | 최대 8회 | 한 행의 모든 수치를 한 번의 집계 쿼리로 조회 |
| v3 | 전체 단일 집계 쿼리 방식 | 1회 | 전체 통계표를 `SUM(CASE WHEN ...)` + `GROUP BY`로 조회 |

## 해석 기준

v2는 실무에서 적용했던 개선 방식과 가장 유사한 버전이다. 화면의 행 구조를 유지하면서 SQL 호출 수를 크게 줄이는 절충안이며, 전체 통계표를 반드시 1개 SQL로 만들어야 한다는 접근은 아니다.

v3는 개인 프로젝트에서 추가로 검증한 방식이다. DB round-trip을 가장 크게 줄일 수 있지만, SQL이 길어지고 통계 규칙이 SQL에 강하게 들어간다. 고정 컬럼 통계표에서는 유효한 선택지가 될 수 있으나 모든 통계 화면의 정답은 아니다.

## 재측정 필요

이전 측정 결과는 현재 v2와 의미가 다르다. 현재 v2는 행 단위 집계 방식이므로 기존 `performance-results`나 이전 문서의 v2 수치는 그대로 사용하지 않는다.

현재 구조에서는 아래 조건으로 다시 측정한다.

| 데이터 수 | 동시 사용자 | 버전 | 평균 응답 시간(ms) | p95(ms) | SQL 실행 수 | SQL 총 시간(ms) | Hikari pending | 비고 |
|---:|---:|---|---:|---:|---:|---:|---:|---|
| 3,200 | 1 | v1 |  |  | 64 |  |  |  |
| 3,200 | 1 | v2 |  |  | 8 |  |  | 재측정 필요 |
| 3,200 | 1 | v3 |  |  | 1 |  |  |  |
| 3,200 | 10 | v1 |  |  | 64 |  |  |  |
| 3,200 | 10 | v2 |  |  | 8 |  |  | 재측정 필요 |
| 3,200 | 10 | v3 |  |  | 1 |  |  |  |
| 100,000 | 10 | v1 |  |  | 64 |  |  |  |
| 100,000 | 10 | v2 |  |  | 8 |  |  | 재측정 필요 |
| 100,000 | 10 | v3 |  |  | 1 |  |  |  |

## 재측정 명령

```bash
VERSION=v1 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=1 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js

VERSION=v1 VUS=10 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v2 VUS=10 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
VERSION=v3 VUS=10 DURATION=30s SLEEP=1 BASE_URL=http://localhost:8080 k6 run performance/k6/stat.js
```

## 해석 질문

- 응답 시간이 줄어든 원인이 쿼리 수 감소인지, 인덱스 효과인지, 캐시 효과인지?
- SQL 실행 횟수는 줄었지만 행 단위 또는 단일 집계 SQL 시간이 길어지지는 않았는지?
- 커넥션 풀 pending이 발생했는지?
- rows examined가 얼마나 증가/감소했는지?
- v2의 행 단위 집계 방식이 실제 운영 환경에서 유지보수 가능한지?
- v3의 단일 쿼리보다 v2 또는 목적별 다중 집계 쿼리가 더 나은 상황은 없는지?
