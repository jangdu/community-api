# 실행과 디버그

## 실행 방법

| 방법 | 설명 |
|---|---|
| `main` 메서드 옆 **▶** 클릭 | 첫 실행 시 |
| **Ctrl + R** | 실행 (Run) |
| **Ctrl + D** | 디버그 모드 실행 |
| 터미널: `./gradlew bootRun` | CLI로 실행 |

### NestJS 비유

| NestJS | Spring Boot |
|---|---|
| `npm run start:dev` | Ctrl + R (DevTools가 자동 재시작) |
| `npm run start` | `./gradlew bootRun` |
| `npm run build` | `./gradlew build` |

## DevTools (자동 재시작)

Spring Boot DevTools = nodemon

코드 수정 후 **Cmd + F9** (Build)하면 자동으로 재시작된다.

## 디버그 모드

실무에서는 **디버그 모드(Ctrl + D)** 를 주로 사용한다.

자바의 디버거가 강력해서 `console.log` 대신 브레이크포인트를 찍어 코드 실행을 중간에 멈추고 변수 값을 확인할 수 있다.

## 서버 실행 시 확인사항

서버가 정상 실행되면 콘솔에 아래 로그가 출력된다:

```
Tomcat started on port 8080
Started CommunityApplication in X seconds
```

- 기본 포트: **8080** (NestJS의 3000과 같은 것)
- 접속: `http://localhost:8080`
