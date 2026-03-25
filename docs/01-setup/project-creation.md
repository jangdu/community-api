# IntelliJ에서 Spring Boot 프로젝트 생성

## 1. 새 프로젝트

File → New → Project → 왼쪽에서 **Spring Boot** 선택

## 2. 프로젝트 설정

| 항목 | 설정 | 설명 |
|---|---|---|
| Server URL | `start.spring.io` | 프로젝트 템플릿을 가져오는 서버 (건드릴 필요 없음) |
| Name | `community` | 프로젝트 이름 |
| Language | Java | |
| Type | Gradle - Groovy | 빌드 도구 |
| Group | `com.jangdu` | npm scope 같은 것 |
| Artifact | `community` | package name |
| JDK | Eclipse Temurin 24 | 설치된 JDK |
| Java | 21 | 언어 레벨 (최신 LTS) |
| Packaging | Jar | 내장 톰캣으로 실행 |
| Configuration | YAML | 설정 파일 형식 |

### Server URL이란?

IntelliJ가 프로젝트를 직접 만드는 게 아니라, `start.spring.io` 서버에 요청해서 프로젝트 템플릿을 받아오는 것이다. NestJS의 `nest new` 명령어가 내부적으로 템플릿을 가져오는 것과 같다.

### Configuration: Properties vs YAML

같은 설정을 다른 형식으로 표현하는 것이다.

**Properties:**
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
```

**YAML:**
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

YAML이 계층 구조가 한눈에 보여서 선택했다.

## 3. Spring Boot 버전

- **SNAPSHOT** = 개발 중 (불안정)
- **M1, M2, M3** = 마일스톤 (베타)
- **숫자만 있는 것** = 정식 릴리즈 (안정적)

이 프로젝트에서는 **4.0.4** (정식 릴리즈)를 선택했다.
