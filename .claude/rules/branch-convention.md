# 브랜치 컨벤션

## 브랜치 전략

```
dev (배포)
 └── staging      ← 통합 테스트 브랜치, 모든 작업 브랜치는 여기로 PR
       ├── feat/#12-{브랜치명}
       ├── fix/#15-{브랜치명}
       └── ...
```

- **작업 브랜치**: `staging` 기준으로 생성, 완료 후 `staging` 으로 PR
- **staging**: 통합 테스트 전용. 작업 브랜치들을 머지해 기능 검증
- **dev**: 배포 전용. `staging → dev` PR 완료 시 자동 배포. 직접 커밋 금지

## 브랜치 네이밍

```
{타입}/#{이슈번호}-{브랜치명}
```

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
feat/#12-excel-job-create
fix/#15-polling-stop-condition
refactor/#20-dto-layer-cleanup
chore/#8-docker-compose-setup
docs/#3-api-spec-update
test/#25-excel-job-service-test
```

## 브랜치 생성 명령

```bash
# staging 기준으로 브랜치 생성
git checkout staging
git pull origin staging
git checkout -b feat/#12-excel-job-create
```

## PR 방향

```
작업 브랜치 → staging   (기능 완료 후)
staging     → dev       (테스트 완료 후, 배포)
```
