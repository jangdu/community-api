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
| 순수 단위 | 어노테이션 없음 | 순수 jest 테스트 | 가장 빠름 |
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
// fixture/UserFixture.java — 상수 + 팩토리 메서드
public class UserFixture {
    public static final String EMAIL = "test@test.com";
    public static final String PASSWORD = "Test1234!";

    public static User createUser() { ... }
    public static SignupRequest createSignupRequest() { ... }
}

// 테스트에서
User user = UserFixture.createUser();
SignupRequest request = UserFixture.createSignupRequest();
```

DTO에 `@AllArgsConstructor`를 추가해야 테스트에서 `ReflectionTestUtils` 없이 생성할 수 있다.

### 7. @WebMvcTest에서 Security

`@WebMvcTest`는 Controller만 로드하기 때문에 Security 관련 빈을 별도로 제공해야 한다. `TestSecurityConfig`를 만들어서 `@Import`한다.

```java
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)    // 테스트용 Security 설정
class AuthControllerTest { ... }
```

프로덕션 SecurityConfig는 CORS, Redis 등 외부 의존성이 있어서 `@WebMvcTest`에서 로드하기 어렵다. 테스트용 설정을 분리하는 게 정석이다.

### 8. 순수 단위 테스트 (Spring 컨텍스트 없이)

외부 의존성 없는 클래스는 Spring 어노테이션 없이 순수하게 테스트한다. JwtProvider처럼 내부 로직만 검증하면 되는 경우.

```java
class JwtProviderTest {
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // 직접 객체 생성, Spring 컨텍스트 안 띄움
        JwtProperties properties = new JwtProperties();
        properties.setSecret("...");
        jwtProvider = new JwtProvider(properties);
        jwtProvider.init();
    }
}
```

가장 빠르고, 의존성이 적어서 깨지기 어렵다.

## 현재 테스트 현황

| 클래스 | 방식 | 테스트 수 | 검증 대상 |
|---|---|---|---|
| AuthServiceTest | Mockito 단위 | 9개 | 회원가입/로그인/재발급/로그아웃 로직 |
| AuthControllerTest | @WebMvcTest | 5개 | HTTP 요청/응답, Validation, Security |
| JwtProviderTest | 순수 단위 | 7개 | 토큰 생성/검증/파싱 |
| UserServiceTest | Mockito 단위 | 4개 | 유저 조회 로직 |
| CommunityApplicationTests | @SpringBootTest | 1개 | 컨텍스트 로드 |

총 **26개** 테스트.

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
- `ObjectMapper` 빈이 `@WebMvcTest`에서 자동 등록되지 않음 — `new ObjectMapper()`로 직접 생성
- Lombok이 test 소스에서 동작하지 않을 수 있음 — test config 클래스에서는 직접 생성자 작성
