# 설정 파일 (Configuration)

## 프로필 분리

NestJS에서 `.env`, `.env.local`, `.env.production`으로 환경별 설정을 나누듯이, Spring Boot도 동일하게 분리한다.

```
application.yaml              ← 공통 설정 (git에 올림)
application-local.yaml         ← 로컬 개발용 (git에 안 올림)
application-prod.yaml          ← 운영용 (git에 안 올림)
```

| NestJS | Spring Boot |
|---|---|
| `.env` | `application.yaml` |
| `.env.local` | `application-local.yaml` |
| `.env.production` | `application-prod.yaml` |
| `NODE_ENV=production` | `spring.profiles.active=prod` |

## .gitignore

민감한 정보(DB 비밀번호, API 키 등)가 담긴 파일은 git에 올리지 않는다.

```gitignore
application-local.yaml
application-prod.yaml
```

## application.yaml에는 뭘 넣나?

- 공통 설정 (포트 번호, 앱 이름 등)
- 민감하지 않은 설정

## application-local.yaml에는 뭘 넣나?

- DB 접속 정보 (host, password)
- 로컬 개발용 API 키
- 디버그 설정
