# 프로젝트 구조

## 현재 파일 구조

```
community/
├── build.gradle                          ← package.json (의존성 + 설정)
├── settings.gradle                       ← workspace 설정
├── gradlew / gradlew.bat                 ← npx 같은 것 (Gradle 실행기)
├── gradle/                               ← Gradle 래퍼 파일
└── src/
    ├── main/
    │   ├── java/com/jangdu/community/
    │   │   └── CommunityApplication.java ← main.ts (진입점)
    │   └── resources/
    │       └── application.yaml          ← .env (설정 파일)
    └── test/
        └── java/com/jangdu/community/
            └── CommunityApplicationTests.java
```

## NestJS와 비교

| NestJS | Spring Boot |
|---|---|
| `src/` | `src/main/java/` |
| `test/` | `src/test/java/` |
| `src/main.ts` | `CommunityApplication.java` |
| `.env` | `application.yaml` |
| `package.json` | `build.gradle` |

## 앞으로 만들어갈 폴더 구조

NestJS는 모듈별로 폴더를 나누지만, Spring Boot는 **레이어별로 나누는 것이 국룰**이다.

```
com/jangdu/community/
├── controller/    ← NestJS의 *.controller.ts
├── service/       ← NestJS의 *.service.ts
├── repository/    ← NestJS의 *.repository.ts
├── entity/        ← NestJS의 *.entity.ts
├── dto/           ← NestJS의 *.dto.ts
├── config/        ← Security, DB 등 설정
└── exception/     ← 글로벌 에러 핸들러
```

## CommunityApplication.java (진입점)

```java
@SpringBootApplication    // NestJS의 @Module + bootstrap 역할
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
        // NestJS의 NestFactory.create(AppModule) 과 같은 것
    }
}
```
