# Flyway 마이그레이션

## TypeORM Migration과 다른 점

| TypeORM | Flyway |
|---|---|
| `migration:generate`로 자동 생성 | **SQL 직접 작성** |
| TypeScript로 작성 | SQL로 작성 |
| 타임스탬프 기반 파일명 | `V{버전}__{설명}.sql` (언더스코어 2개) |
| `migration:run` 수동 실행 | **서버 시작 시 자동 실행** |

## 핵심 규칙

1. 한번 실행된 파일은 **절대 수정 금지** (체크섬 불일치로 서버 에러)
2. 변경 필요하면 새 파일 추가 (`V2__...`, `V3__...`)
3. `ddl-auto`는 반드시 `none`으로 설정

## Spring Boot 4.x 주의사항

Spring Boot 4.x에서는 `flyway-core`만으로는 자동설정이 안 된다. `spring-boot-flyway` 모듈이 별도로 필요하다.

```groovy
implementation 'org.springframework.boot:spring-boot-flyway'
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-mysql'  // MySQL 사용 시
```
