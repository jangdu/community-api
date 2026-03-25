# 의존성 (Dependencies)

NestJS에서 `npm install @nestjs/typeorm` 하는 것처럼, Spring Boot도 필요한 기능을 의존성으로 추가한다.

## 선택한 의존성 목록

| 의존성 | NestJS 비유 | 역할 |
|---|---|---|
| **Lombok** | - | getter/setter 등 보일러플레이트 코드 자동 생성 |
| **Spring Boot DevTools** | nodemon | 코드 수정 시 자동 재시작 |
| **Spring Web** | Express / Fastify | REST API 처리 |
| **Spring Security** | Passport + Guard | 인증/인가, 보안 |
| **Spring Data JPA** | TypeORM / Prisma | ORM (DB 연동) |
| **Validation** | class-validator | DTO 유효성 검사 |
| **MySQL Driver** | mysql2 패키지 | MySQL 연결 드라이버 |
| **H2 Database** | SQLite | 내장 DB (테스트용) |
| **Spring Boot Actuator** | terminus (헬스체크) | 모니터링, 헬스체크 엔드포인트 |

## 의존성 추가/제거

프로젝트 생성 후에도 `build.gradle`에서 한 줄 추가/제거하면 된다. `npm install/uninstall`과 같다.

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // ↑ npm install @nestjs/common 같은 것
}
```

나중에 필요한 기능(Redis, WebSocket, Mail 등)은 그때그때 추가하면 된다.
