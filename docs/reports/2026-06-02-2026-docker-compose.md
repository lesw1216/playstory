# Docker Compose 전체 스택 구성

> 2026-06-02 20:26

`docker compose up --build` 한 번으로 MySQL · 백엔드 · 프론트가 함께 뜨도록 compose와 Dockerfile들을 작성했다.

## 작업 요청 요약

- 루트에 docker-compose 작성(설계 §1-4: 한 번의 실행으로 전체 서비스).
- 스코프: MySQL + 백엔드 + 프론트(nginx 정적 서빙).
- compose 실행은 사용자가 직접 — 파일 작성 + 정적 검증 + 보고서까지.

## 생성 파일

| 파일 | 내용 |
|------|------|
| `docker-compose.yml` | 루트. mysql/backend/frontend 3서비스 + 볼륨 2개 |
| `backend/Dockerfile` | 멀티스테이지(temurin17 jdk 빌드 → jre 실행), bootJar |
| `backend/.dockerignore` | .gradle/build/data 제외 |
| `frontend/Dockerfile` | 멀티스테이지(node22 빌드 → nginx 서빙) |
| `frontend/nginx.conf` | 정적 서빙 + SPA fallback |
| `frontend/.dockerignore` | node_modules/dist 제외 |

## 구성 핵심

- **mysql**: `utf8mb4`(한글 시드 보존), `./db/init`→`/docker-entrypoint-initdb.d`로 스키마+10만 시드 최초 1회 실행, `mysql-data` 볼륨, `mysqladmin ping` healthcheck.
- **backend**: DB접속·`EXCEL_OUTPUT_DIR=/app/data/excel`를 *env로 주입* → application.yaml을 Spring relaxed binding이 override(**코드 변경 없음**). `excel-data` 볼륨, `depends_on: mysql(service_healthy)`, `deploy.resources.limits` cpus 0.5 / mem 1G(설계 요구).
- **frontend**: nginx로 빌드 산출물 서빙, `5173:80` → 브라우저 origin `http://localhost:5173`가 WebConfig CORS 허용값과 일치. API는 브라우저가 `localhost:8080` 직접 호출.

## 검증 (정적)

- compose YAML 파싱 OK — services(mysql/backend/frontend), volumes(mysql-data/excel-data).
- `docker compose config` → **스키마 유효**.
- build context·마운트 소스(Dockerfile, nginx.conf, db/init/*.sql) 존재 확인.

## 사용자 실행 가이드

```
docker compose up --build        # 최초: 10만 시드 주입으로 mysql healthy까지 수십 초
# → http://localhost:5173 접속, "엑셀 생성 요청" → PENDING→PROCESSING→DONE polling

docker compose exec backend ls /app/data/excel   # 생성된 .xlsx 확인
docker compose down              # 중지(데이터 볼륨 유지)
docker compose down -v           # 완전 초기화(시드 재실행됨)
```

## 알려진 제약 · 주의

- 최초 기동은 10만건 시드 주입으로 mysql healthy까지 시간이 걸린다(healthcheck `start_period 60s`).
- DB 비밀번호는 이미 repo의 application.yaml에 존재 → compose에 인라인(신규 노출 없음). 운영 시 `.env` 분리 권장.
- `backend/data`(로컬 실행분)는 gitignore됨. 컨테이너 엑셀은 `excel-data` 볼륨에 별도 보관.
- 커밋은 사용자 지시 따름(미지시 시 안 함).

> 후속 참고: 이후 same-origin 프록시(nginx 리버스 프록시) 전환으로 프론트는 `localhost:8080` 직접 호출 대신 `/api`를 nginx가 백엔드로 중계하도록 변경됨(CORS 불필요).
