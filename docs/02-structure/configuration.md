# 설정 파일 (Configuration)

## 프로필 분리

```
application.yaml              ← 공통 설정 (git에 올림)
application-local.yaml         ← 로컬 개발용 (git에 안 올림)
application-prod.yaml          ← 운영용 (git에 안 올림)
```

| NestJS | Spring Boot |
|---|---|
| `.env` | `application.yaml` |
| `.env.local` | `application-local.yaml` |
| `.env.production` | `application-prod.yaml` |
| `NODE_ENV=production` | `spring.profiles.active=prod` |

## application.yaml (공통)

```yaml
spring:
  application:
    name: community
  profiles:
    active: local        # 기본 프로필
  jpa:
    open-in-view: false  # 안티패턴 비활성화
```

`open-in-view: false`는 중요하다. 기본값이 `true`인데, 이 경우 View 렌더링 중 DB 쿼리가 실행될 수 있어 성능 문제가 생긴다.

## application-local.yaml (로컬 개발용)

DB, Redis, JWT 등 환경별로 다른 설정을 넣는다. `.gitignore`에 추가해서 git에 올라가지 않도록 한다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/community
    username: root
    password: 1234
  jpa:
    hibernate:
      ddl-auto: none     # Flyway가 스키마를 관리하므로 none
    show-sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true
  data:
    redis:
      host: localhost
      port: 6380
      password: redis1234

jwt:
  secret: ...
  access-token-expiry: 1800000      # 30분
  refresh-token-expiry: 604800000   # 7일

logging:
  level:
    com.jangdu.community: DEBUG
```

## @ConfigurationProperties

환경 변수를 타입 안전하게 바인딩하는 방식. NestJS의 `ConfigService.get()`과 비슷하지만, 컴파일 타임에 타입이 보장된다.

```java
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenExpiry;
    private long refreshTokenExpiry;
}
```

`@ConfigurationPropertiesScan`을 메인 클래스에 붙이면 자동으로 스캔된다.
