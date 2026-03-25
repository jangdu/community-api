# Redis - Spring 특이사항

## NestJS와 다른 점

| NestJS | Spring Boot |
|---|---|
| `ioredis` / `redis` 패키지 | `spring-boot-starter-data-redis` (Lettuce 내장) |
| `redis.set(key, value, 'EX', ttl)` | `redisTemplate.opsForValue().set(key, value, ttl, TimeUnit)` |
| 직접 Redis 클라이언트 사용 | `RedisTemplate`을 통해 사용 |

## RedisTemplate

Spring에서 Redis를 다루는 핵심 객체. `RedisConfig`에서 직렬화 방식을 지정해야 한다.

```java
@Bean
public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
}
```

직렬화를 안 하면 Redis에 `\xac\xed\x00\x05...` 같은 바이너리가 저장된다.

## JPA와 Redis 동시 사용 시

Spring Data JPA와 Spring Data Redis를 함께 쓰면 Repository 스캔이 겹친다. `@Entity` 어노테이션이 있는 JPA 엔티티를 Redis가 인식하려고 시도하면서 경고가 뜬다. 동작에는 문제없다.
