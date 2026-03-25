# 예외 처리 - Spring 특이사항

## NestJS와 다른 점

### 1. @RestControllerAdvice

NestJS의 `ExceptionFilter`와 비슷하지만, 별도로 등록할 필요 없이 `@RestControllerAdvice` 붙이면 전역 적용된다.

```java
@RestControllerAdvice  // 이거 하나로 모든 컨트롤러의 예외를 잡는다
public class GlobalExceptionHandler { ... }
```

### 2. ErrorCode를 enum으로 관리

NestJS에서는 `throw new ConflictException('메시지')` 하지만, Spring에서는 ErrorCode enum으로 관리하면 코드 일관성이 높아진다.

```java
throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
// → { status: 409, code: "A001", message: "이미 존재하는 이메일입니다" }
```

새 에러 추가 시 `ErrorCode`에 한 줄만 추가하면 된다.

### 3. Validation 에러 처리 차이

NestJS는 `class-validator`가 알아서 400 에러를 던지지만, Spring은 `MethodArgumentNotValidException`이 발생하고 이를 `GlobalExceptionHandler`에서 잡아야 한다.

### 4. Security 예외는 별도

Spring Security에서 발생하는 401/403은 `GlobalExceptionHandler`를 **거치지 않는다**. Security Filter 레벨에서 발생하기 때문. `SecurityConfig`에서 별도 처리가 필요하다.

## 응답 형식 통일

성공과 에러 모두 통일된 wrapper를 사용한다.

- 성공: `ApiResponse<T>` — `{ status, message, data, timestamp }`
- 에러: `ErrorResponse` — `{ status, code, message, errors?, timestamp }`
