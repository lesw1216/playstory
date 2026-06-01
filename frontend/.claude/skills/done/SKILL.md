---
name: done
description: 프론트엔드의 현재 진행 중인 task를 마무리한다. (type-check → lint → build → task 완료 → commit)
---

타입체크 → lint → 빌드 → task 파일 완료 처리 → git commit 순서로 실행한다.
(프론트엔드는 테스트 프레임워크 미설치로 `/test` 단계가 없다.)

## 실행 순서

### 1. 타입체크

```bash
cd frontend
npm run type-check
```

타입 오류가 있으면 중단하고 보고한다. 커밋하지 않는다.

### 2. Lint

```bash
cd frontend
npm run lint
```

`--fix` 로도 해소되지 않는 오류가 있으면 중단하고 보고한다.

### 3. 빌드

```bash
cd frontend
npm run build
```

빌드 실패 시 중단하고 오류를 보고한다. 커밋하지 않는다.

### 4. task 파일 완료 처리 및 TodoWrite 동기화

`docs/tasks/` 에서 현재 작업 중인 task 파일을 찾아 아래를 수행한다.

- 모든 항목을 `- [x]` 로 변경
- 완료일 추가 (`> 완료일: YYYY-MM-DD`)

task 파일 수정 후 TodoWrite도 동일하게 모든 항목을 `completed` 로 업데이트한다.

### 5. git commit

전역 commit 스킬(`@~/.claude/skills/commit/SKILL.md`)에 위임한다.
task 파일 변경사항도 함께 스테이징 대상에 포함한다.

## 주의사항

- 타입체크·lint·빌드 중 하나라도 실패하면 즉시 중단한다. 사용자 확인 없이 다음 단계로 넘어가지 않는다.
- lint 오류를 무시하기 위해 `eslint-disable` 을 남발하지 않는다. 원인을 먼저 해결한다.
- task 파일이 없으면 사용자에게 알리고 커밋만 진행할지 확인한다.
