# 프로젝트 구조

## 전체 파일 구조

```
community/
├── build.gradle                          ← package.json (의존성 + 설정)
├── settings.gradle                       ← workspace 설정
├── docker-compose.yml                    ← Docker 서비스 (MySQL, Redis)
├── .editorconfig                         ← 코드 스타일 통일
├── .gitignore
├── gradlew / gradlew.bat                 ← Gradle 실행기
├── docs/                                 ← 학습 문서
└── src/
    ├── main/
    │   ├── java/com/jangdu/community/
    │   │   ├── CommunityApplication.java ← main.ts (진입점)
    │   │   ├── auth/                     ← 인증 도메인
    │   │   ├── user/                     ← 유저 도메인
    │   │   └── global/                   ← 공통 설정/예외
    │   └── resources/
    │       ├── application.yaml          ← 공통 설정
    │       ├── application-local.yaml    ← 로컬 환경 설정 (git 제외)
    │       └── db/migration/             ← Flyway 마이그레이션 SQL
    └── test/
        └── java/com/jangdu/community/
```

## 도메인별 구조 (NestJS 모듈 방식)

레이어별 구조(controller/, service/, entity/ 를 최상위에 두는 방식)가 아닌, **도메인별 구조**를 채택했다. NestJS의 모듈 구조와 동일한 사상이다.

```
com/jangdu/community/
├── auth/                              ← 인증 도메인 (NestJS의 auth 모듈)
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   ├── service/RefreshTokenService.java
│   ├── dto/
│   │   ├── SignupRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RefreshRequest.java
│   │   └── TokenResponse.java
│   └── jwt/
│       ├── JwtProvider.java
│       ├── JwtProperties.java
│       └── JwtAuthenticationFilter.java
├── user/                              ← 유저 도메인 (NestJS의 user 모듈)
│   ├── controller/UserController.java
│   ├── service/UserService.java
│   ├── entity/User.java
│   ├── repository/UserRepository.java
│   └── dto/UserResponse.java
├── global/                            ← 공통 모듈
│   ├── common/ApiResponse.java        ← 통일된 API 응답 래퍼
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── RedisConfig.java
│   │   └── JpaAuditingConfig.java
│   ├── entity/BaseTimeEntity.java     ← createdAt, updatedAt 공통 엔티티
│   └── exception/
│       ├── ErrorCode.java             ← 에러 코드 enum
│       ├── ErrorResponse.java         ← 에러 응답 형식
│       ├── BusinessException.java     ← 커스텀 예외
│       └── GlobalExceptionHandler.java
└── CommunityApplication.java
```

## NestJS와 비교

| NestJS | Spring Boot |
|---|---|
| `src/` | `src/main/java/` |
| `test/` | `src/test/java/` |
| `src/main.ts` | `CommunityApplication.java` |
| `.env` | `application.yaml` |
| `package.json` | `build.gradle` |
| `src/auth/` 모듈 | `auth/` 패키지 |
| `src/user/` 모듈 | `user/` 패키지 |
| 공통 Guard, Filter | `global/` 패키지 |

## CommunityApplication.java (진입점)

```java
@SpringBootApplication
@ConfigurationPropertiesScan  // @ConfigurationProperties 클래스 자동 스캔
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }
}
```
