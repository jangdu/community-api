# JWT - Spring 특이사항

## NestJS와 다른 점

### 1. SecretKey 초기화

NestJS에서는 `jwt.sign(payload, secret)` 하면 끝이지만, Java에서는 SecretKey 객체를 만들어야 한다.

```java
@PostConstruct
protected void init() {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}
```

`@PostConstruct` — Bean이 생성된 후 한 번 실행. 매 요청마다 Key를 생성하지 않도록 초기화 시 한 번만 생성한다.

### 2. @ConfigurationProperties

NestJS의 `ConfigService.get('JWT_SECRET')`과 달리, 자바는 타입이 보장되는 Properties 클래스를 만든다.

```java
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;          // jwt.secret
    private long accessTokenExpiry; // jwt.access-token-expiry (kebab → camel 자동 변환)
}
```

yaml의 `kebab-case`가 자바의 `camelCase`로 자동 매핑된다.

### 3. Filter 등록

NestJS에서는 Guard를 `@UseGuards()`로 붙이지만, Spring에서는 SecurityConfig에서 Filter를 등록한다.

```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

`UsernamePasswordAuthenticationFilter` 앞에 우리 JWT 필터를 넣겠다는 뜻.

### 4. 인증 정보 접근

```java
// NestJS: @Req() req → req.user.id
// Spring: Authentication 주입
@PostMapping("/logout")
public ResponseEntity<?> logout(Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
}
```
