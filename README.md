# MyBatis 통계 쿼리 최적화 예제

> 셀 단위 COUNT 반복 호출 구조를 재현하고, 행 단위 집계와 단일 집계 방식의 성능·유지보수성 차이를 비교한 프로젝트입니다.

## 프로젝트 배경

실무에서 통계 페이지 응답 지연 문제를 분석하던 중, 통계표의 각 셀마다 COUNT 쿼리가 반복 실행되는 구조를 확인했습니다.  
이 프로젝트는 해당 문제를 공개 가능한 고객 문의 통계 예제로 단순화하여 재현하고, 여러 개선 방식을 비교하기 위해 만들었습니다.

## 개선 목표

- 반복 COUNT 쿼리 구조 재현
- SQL 실행 횟수 감소 방식 비교
- DB round-trip, SQL 복잡도, 유지보수성의 trade-off 확인
- 성능 측정 기준과 해석 방법 정리

## 기술 스택

- Java 21
- Spring Boot 3.5.14
- MyBatis
- MySQL
- Thymeleaf
- k6

## 버전 비교

| 버전 | 방식 | SQL 실행 수 | 목적 |
|---|---:|---:|---|
| v1 | 셀 단위 COUNT | 최대 64회 | 개선 전 문제 구조 재현 |
| v2 | 행 단위 집계 | 최대 8회 | 실무 개선 방식과 가장 유사 |
| v3 | 전체 단일 집계 | 1회 | 추가 개선 가능성 검증 |

## 실행 방법

```bash
docker compose up -d
./gradlew bootRun
```

접속 URL:
```
http://localhost:8080/inquiries/stats/v1
http://localhost:8080/inquiries/stats/v2
http://localhost:8080/inquiries/stats/v3
```

상세 문서
- [문제 상황과 프로젝트 배경](docs/problem-context.md)
- [v1/v2/v3 쿼리 전략 비교](docs/query-strategies.md)
- [성능 테스트 계획](docs/performance-test-plan.md)
- [성능 측정 결과](docs/performance-results.md)
- [SQL 분석 스크립트](docs/sql)
