# Spring Security

## NestJS와 핵심 차이

NestJS는 Guard를 개별 라우트에 붙이지만, Spring Security는 **Filter Chain**으로 모든 요청을 가로챈다. 개별 컨트롤러에 어노테이션을 붙이는 게 아니라, `SecurityConfig`에서 일괄 설정한다.

## SecurityConfig에서 알아야 할 것

```java
http
    .csrf(csrf -> csrf.disable())       // REST API면 끈다
    .formLogin(form -> form.disable())  // JWT 쓰면 필요 없다
    .httpBasic(basic -> basic.disable()) // JWT 쓰면 필요 없다
    .sessionManagement(session ->
        session.sessionCreationPolicy(STATELESS))  // JWT = Stateless
```

이 4개를 안 끄면 Spring Security 기본 동작(세션 기반 로그인 페이지)이 활성화된다.

## 공개 API 설정 시 주의

```java
// 나쁜 예 — logout도 열려버림
.requestMatchers("/api/auth/**").permitAll()

// 좋은 예 — 필요한 것만 정확히
.requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/refresh").permitAll()
```

## Filter vs Guard

| NestJS Guard | Spring Security Filter |
|---|---|
| 개별 라우트에 `@UseGuards()` | `SecurityConfig`에서 일괄 설정 |
| Guard 안에서 true/false 반환 | Filter에서 SecurityContext에 인증 정보 저장 |
| 순서 신경 안 써도 됨 | `.addFilterBefore()`로 순서 지정 필요 |

## exceptionHandling

Spring Security에서 발생하는 401/403은 `GlobalExceptionHandler`를 거치지 않는다. Filter 레벨에서 발생하기 때문. 그래서 `SecurityConfig`에서 별도로 처리해야 한다.

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint(...)   // 401 처리
    .accessDeniedHandler(...)        // 403 처리
)
```
