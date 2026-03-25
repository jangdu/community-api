# JDK와 빌드 도구

## JDK (Java Development Kit)

Node.js가 자바스크립트 런타임인 것처럼, JDK는 자바 코드를 실행하는 도구다.

| Node.js | Java |
|---|---|
| Node.js | JDK |
| Node.js v22 | JDK 24 |
| `tsconfig`의 `target` | Java 언어 레벨 |

### JDK 버전과 언어 레벨의 차이

- **JDK** = 설치된 개발 도구 버전 (예: Temurin 24)
- **Java 언어 레벨** = 코드에서 사용할 문법 범위 (예: 21)

JDK 24가 설치되어 있어도 언어 레벨을 21로 설정하면, Java 21까지의 문법만 사용하겠다는 뜻이다.

### LTS (Long Term Support)

Node.js와 동일한 개념. 장기 지원 버전.

| 버전 | 상태 |
|---|---|
| JDK 17 | 이전 LTS |
| **JDK 21** | **최신 LTS (현재 실무 표준)** |
| JDK 24 | 최신 릴리즈 (비LTS) |

## 빌드 도구

Node.js의 npm/yarn 역할을 하는 것이 빌드 도구다.

### npm과 비교

```
package.json    →  build.gradle         (의존성 + 프로젝트 설정)
node_modules/   →  ~/.m2/repository/    (로컬 캐시)
npm install     →  gradle build         (의존성 다운로드 + 빌드)
npmjs.com       →  Maven Central        (패키지 저장소)
npm run dev     →  gradle bootRun       (앱 실행)
```

### Maven vs Gradle

| | Maven | Gradle |
|---|---|---|
| 설정 파일 | `pom.xml` (XML) | `build.gradle` (Groovy/Kotlin) |
| 장점 | 레퍼런스 많음, 안정적 | 간결함, 빠른 빌드, 유연함 |
| 점유율 | 레거시/엔터프라이즈 | 신규 프로젝트에서 선호 추세 |

이 프로젝트에서는 **Gradle - Groovy**를 사용한다.
