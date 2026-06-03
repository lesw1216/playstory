# .claude 설정 모노레포 분리 + 도메인 재작성

> 2026-06-01 17:05

이전 프로젝트(Picket) 백엔드 전용 설정을 공통/백엔드/프론트로 분리하고, 모든 예시를 PlayStory 도메인으로 교체했다.

## 작업 요청 요약

- 루트 `.claude/` 에 Picket 프로젝트의 백엔드 전용 skills/rules가 그대로 있었음.
- 공통(git/process)은 루트 `.claude/`, 백엔드 전용은 `backend/.claude/`, 프론트 전용은 `frontend/.claude/` 로 분리.
- 각 md의 예시를 이전 프로젝트 용어/도메인에서 현재 프로젝트(설계.md 기준)로 재작성.
- 프론트는 설정이 없어 프로젝트 분석 후 신규 작성.
- 커밋은 하지 않음.

## 사전 계획

공식 문서로 nested `.claude` 로딩 메커니즘을 먼저 검증한 뒤 레이아웃 확정:

- `backend/.claude/CLAUDE.md`·`frontend/.claude/CLAUDE.md` 는 해당 서브트리 접근 시 on-demand 자동 로드.
- `.claude/rules/*.md` 는 import 없이 instructions로 자동 로드(루트=시작 시, 서브트리=on-demand).
- skills는 nested `.claude/skills/` 에서 on-demand 발견, 동명은 가장 가까운 scope 우선 → 서브프로젝트별 `done` 공존 가능.

도메인 매핑: `com.picketlogia.picket → com.playstory.backend`, product/reservation/seat/seller → `excelJob / order / errorLog`, 상태 → `PENDING/PROCESSING/DONE/FAILED`, CORS 운영도메인·UserType 제거(설계에 없음).

프론트 테스트: vitest 미설치 → 테스트 rule/skill 생략(사용자 확인).

## 변경 파일 목록

### 루트 `.claude/` (공통)

| 경로 | 유형 | 요약 |
|------|------|------|
| `CLAUDE.md` | ~ | 모노레포 개요로 축약, 스택 상세는 서브프로젝트로 위임 |
| `rules/branch-convention.md` | ~ | 예시 브랜치명 PlayStory로 |
| `rules/task-driven-development.md` | ~ | 예시 작업명 PlayStory로 |
| `skills/task/SKILL.md` | ~ | 도메인 중립화, 서브프로젝트 /test·/done 안내 |
| `skills/pr/SKILL.md` | = | 도메인 용어 없어 유지(이동만) |
| `report-style.md` | ~ | `skills/` 아래 → 루트로 이동 |
| `rules/{coding-convention,dto-create,dto-layer,env,error-handling,openapi,testing}.md` | - | backend로 이동(원본 삭제) |
| `skills/{test,done}/` | - | backend로 이동(원본 삭제) |

### `backend/.claude/` (신규 트리)

| 경로 | 유형 | 요약 |
|------|------|------|
| `CLAUDE.md` | + | 백엔드 스택·명령어·비동기 job 흐름·도메인 |
| `rules/coding-convention.md` | + | 패키지/도메인 재작성, CORS 운영도메인·UserType 제거 |
| `rules/dto-create.md` · `dto-layer.md` | + | ExcelJob/Order DTO 예시 |
| `rules/error-handling.md` | + | BaseResponseStatus 코드 ExcelJob/Order 계열 |
| `rules/env.md` | + | MySQL, DB명 playstory |
| `rules/openapi.md` | + | PlayStory API, /api/excel-jobs 경로 |
| `rules/testing.md` | + | ExcelJobService 예시, com.playstory.backend 경로 |
| `skills/test/SKILL.md` · `done/SKILL.md` | + | gradle 기반, 경로/예시 재작성 |

### `frontend/.claude/` (신규 작성)

| 경로 | 유형 | 요약 |
|------|------|------|
| `CLAUDE.md` | + | Vue3/TS/Vite/Pinia 스택, polling 화면 흐름 |
| `rules/coding-convention.md` | + | SFC script setup, 네이밍, 디렉터리, @ alias |
| `rules/api-and-state.md` | + | native fetch, BaseResponse<T> 타입, job store, polling 규칙 |
| `skills/done/SKILL.md` | + | type-check → lint → build (test 단계 없음) |

## 검증 결과

### 구조 확인

`find .claude backend/.claude frontend/.claude -type f` → 루트 6개 · 백엔드 9개 · 프론트 4개, 의도와 일치.

### 잔존 구도메인 용어 스캔

```
grep -rin "picketlogia|picket|reservation|seat|seller|공연|예매|좌석|판매자" \
  .claude backend/.claude frontend/.claude
=> (none)
```

✓ 잔존 0건. ✓ 각 `@rules/...` 참조는 동일 트리 rules 디렉터리 내에서 해소됨.

## 알려진 제약 · follow-up

- 코드 변경은 없음(설정/문서만) → 빌드·테스트 미수행. 커밋도 하지 않음(사용자 지시).
- 프론트엔드 테스트: vitest 미설치로 `/test` rule/skill 생략. 도입 시 별도 협의 후 추가 필요.
- 백엔드 예시의 패키지 구조(`api/exceljob/...`)·엔티티 메서드는 **제안 컨벤션**이며 실제 구현 코드는 아직 없음. 구현 시 이 규칙을 기준으로 작성.
- Excel 생성 라이브러리는 `build.gradle` 미포함 — 추가 전 협의 필요(기존 주의사항 유지).
- `docs/api/openapi.yaml` 은 아직 미생성. 첫 엔드포인트 구현 시 `/open-api` 또는 `@rules/openapi.md` 기준으로 작성.
