# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

특정 테이블의 데이터를 Excel로 내보내는 요청을 **비동기**로 받아 처리하는 시스템 (PlayStory 사전과제).

- 클라이언트가 Excel 생성 요청 → 서버는 즉시 job ID 응답 → 백그라운드에서 Excel 생성 → 클라이언트가 polling으로 상태 조회 → 완료 시 다운로드

설계 상세는 `docs/설계.md` 참조.

---

## 모노레포 구조

```
playstory/
├── backend/    Spring Boot 3.5 · Java 17 · JPA · MySQL 8   → backend/.claude/ 참조
├── frontend/   Vue 3 · TypeScript · Vite · Pinia · Router · Tailwind CSS v4  → frontend/.claude/ 참조
├── docs/       설계.md · api/openapi.yaml · tasks/
└── .claude/    이 파일 + 공통 규칙·스킬
```

- `backend/`, `frontend/` 는 각각 독립 프로젝트다. **스택·명령어·아키텍처·도메인 규칙은 각 서브프로젝트의 `.claude/CLAUDE.md` 와 `.claude/rules/` 에 정의**되어 있고, 해당 디렉터리에서 작업할 때 자동 로드된다.
- 패키지/의존성 추가는 각 디렉터리에서 수행하며, **라이브러리 추가 전 반드시 협의**한다.

---

## 공통 규칙 (`.claude/`)

이 루트 `.claude/` 에는 백/프 공통으로 적용되는 프로세스·git 규칙만 둔다.

| 영역 | 위치 |
|------|------|
| 브랜치 전략·네이밍 | `@rules/branch-convention.md` |
| Task 기반 개발 흐름 | `@rules/task-driven-development.md` |
| PR 생성 | `/pr` 스킬 |
| Task 생성·진행 | `/task` 스킬 |
| 작업 완료 보고서 | `docs/reports/*.md` (아래 참조) |

마무리 스킬(`/test`, `/done`)은 작업 영역에 따라 각 서브프로젝트 `.claude/skills/` 에서 로드된다.

### 작업 완료 보고서

이 프로젝트의 작업 완료 보고서는 **`docs/reports/`** 에 **Markdown**(`YYYY-MM-DD-HHmm-{slug}.md`)으로 저장한다. (글로벌 기본 경로 `.claude/reports/`·HTML 양식을 덮어쓴다.)

---

## 실행 환경

- Docker Compose 기반. `docker compose up --build` 한 번으로 전체 서비스가 실행되어야 한다 (설계 요구사항).
- 외부 계정·외부 서비스에 의존하지 않는다. 엑셀 파일은 컨테이너 로컬 볼륨에 저장한다.
- 로컬에서 backend 단독 기동 시 MySQL(`localhost:3306`)이 떠 있어야 한다.
