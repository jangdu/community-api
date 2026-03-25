# 의존성 (Dependencies)

## 현재 사용 중인 의존성

| 의존성 | NestJS 비유 | 역할 |
|---|---|---|
| **Spring Web** | Express / Fastify | REST API 처리 |
| **Spring Data JPA** | TypeORM / Prisma | ORM (DB 연동) |
| **Spring Security** | Passport + Guard | 인증/인가, 보안 |
| **Spring Data Redis** | ioredis | Redis 연동 (Refresh Token 저장) |
| **Validation** | class-validator | DTO 유효성 검사 |
| **Flyway** | TypeORM Migration | DB 스키마 마이그레이션 |
| **MySQL Driver** | mysql2 | MySQL 연결 드라이버 |
| **H2 Database** | SQLite | 내장 DB (테스트용) |
| **Actuator** | terminus (헬스체크) | 모니터링, 헬스체크 |
| **Lombok** | - | getter/setter 등 보일러플레이트 제거 |
| **DevTools** | nodemon | 코드 수정 시 자동 재시작 |
| **jjwt** | jsonwebtoken | JWT 토큰 생성/검증 |

## 의존성 추가/제거

`build.gradle`에서 한 줄 추가/제거하면 된다.

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // npm install 같은 것
}
```

Gradle 동기화 후 적용된다 (IntelliJ에서 코끼리 아이콘 클릭 또는 Cmd + Shift + I).
