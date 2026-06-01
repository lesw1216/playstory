# 브랜치 컨벤션

## 브랜치 전략

```
main (기준 / 배포)
 ├── feat/excel-job-create
 ├── fix/polling-stop-condition
 └── ...
```

- **기준 브랜치**: `main`. 작업 브랜치는 항상 `main` 에서 분기한다.
- **작업 브랜치**: 기능/수정 단위로 분기 후, 완료되면 `main` 으로 직접 PR 한다.
- `main` 에 직접 커밋하지 않고 작업 브랜치 → `main` PR로만 반영한다.

## 브랜치 네이밍

```
{타입}/{브랜치명}
```

이슈를 운영하는 경우 `{타입}/#{이슈번호}-{브랜치명}` 형태로 이슈번호를 붙일 수 있다(선택).

### 타입

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `refactor` | 코드 개선 |
| `chore` | 빌드·설정·의존성 변경 |
| `docs` | 문서 작성·수정 |
| `test` | 테스트 코드 작성·수정 |

### 예시

```
feat/order-exceljob-entity
fix/excel-job-status-bug
refactor/dto-layer-cleanup
chore/docker-compose-setup
docs/api-spec-update
test/excel-job-service-test
```

## 브랜치 생성 명령

```bash
# main 기준으로 브랜치 생성
git checkout main
git pull origin main
git checkout -b feat/order-exceljob-entity
```

## PR 방향

```
작업 브랜치 → main   (기능 완료 후)
```
