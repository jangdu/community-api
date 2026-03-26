# 테스트 - Spring 특이사항

## NestJS와 다른 점

### 1. 파일 위치

NestJS는 소스 옆에 `.spec.ts`를 두지만, 자바는 `src/test`에 미러링한다.

```
NestJS:
src/auth/auth.service.spec.ts     ← 소스 옆에

Spring:
src/main/java/.../auth/service/AuthService.java
src/test/java/.../auth/service/AuthServiceTest.java   ← 같은 패키지 구조로 미러링
```

빌드 시 테스트 코드가 프로덕션 jar에 포함되지 않도록 분리하는 구조다. Gradle이 이 구조를 강제한다.

### 2. 테스트 계층 분리

NestJS에서는 하나의 spec에서 mock + supertest를 섞어 쓰지만, Spring은 계층별로 테스트 어노테이션이 다르다.

| 계층 | 어노테이션 | NestJS 비유 | 속도 |
|---|---|---|---|
| Service | `@ExtendWith(MockitoExtension.class)` | `jest.mock()` 단위 테스트 | 빠름 |
| Controller | `@WebMvcTest` | `supertest` | 중간 |
| Repository | `@DataJpaTest` | DB 연결 테스트 | 중간 |
| 통합 | `@SpringBootTest` | E2E 테스트 | 느림 |

각 계층이 자기 책임만 테스트한다. Service 테스트에서 DB를 띄우지 않고, Controller 테스트에서 비즈니스 로직을 검증하지 않는다.

### 3. Mock 방식

```java
// NestJS: jest.mock()
// Spring: Mockito

@Mock                    // jest.fn() 과 같음
private UserRepository userRepository;

@InjectMocks             // 의존성 주입 자동 처리
private AuthService authService;

given(userRepository.findByEmail("test@test.com"))   // jest.mockReturnValue()
    .willReturn(Optional.of(user));

verify(userRepository).save(any());                   // expect(repo.save).toHaveBeenCalled()
```

### 4. BDD 스타일 (given-when-then)

```java
@Test
@DisplayName("이미 존재하는 이메일이면 DUPLICATE_EMAIL 예외를 던진다")
void failWhenDuplicateEmail() {
    // given — 테스트 데이터 준비
    SignupRequest request = UserFixture.createSignupRequest();
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    // when & then — 실행 + 검증
    assertThatThrownBy(() -> authService.signup(request))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getErrorCode())
            .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
}
```

### 5. @Nested로 그룹핑

NestJS의 `describe` 블록과 같다.

```java
// NestJS: describe('회원가입', () => { it('성공', ...) })
// Spring:
@Nested
@DisplayName("회원가입")
class Signup {
    @Test
    @DisplayName("성공 시 토큰을 발급한다")
    void success() { ... }
}
```

### 6. Fixture 패턴

테스트 데이터 생성을 한 곳에서 관리한다. NestJS에서 factory 함수 만들어 쓰는 것과 같다.

```java
// fixture/UserFixture.java
public class UserFixture {
    public static User createUser() { ... }
    public static SignupRequest createSignupRequest() { ... }
}

// 테스트에서
User user = UserFixture.createUser();
```

### 7. @WebMvcTest에서 Security

`@WebMvcTest`는 Controller만 로드하기 때문에 Security 관련 빈을 별도로 제공해야 한다. `TestSecurityConfig`를 만들어서 `@Import`한다.

```java
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)    // 테스트용 Security 설정
class AuthControllerTest { ... }
```

## 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "com.jangdu.community.auth.service.AuthServiceTest"

# IntelliJ: 테스트 클래스에서 Ctrl + Shift + R
```

## Spring Boot 4.x 주의사항

- `@WebMvcTest`, `@AutoConfigureMockMvc` 패키지가 변경됨
  - `org.springframework.boot.test.autoconfigure.web.servlet` → `org.springframework.boot.webmvc.test.autoconfigure`
- `ObjectMapper` 빈이 자동 등록되지 않음 — 직접 `new ObjectMapper()` 생성
