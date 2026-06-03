# 백엔드 CORS 설정 추가

> 2026-06-02 14:56

프론트(5173) → 백엔드(8080) cross-origin 호출의 CORS 오류 해결. `WebMvcConfigurer` 전역 CORS 매핑 추가.

## 원인 진단

- CORS는 **서버가 아니라 브라우저**가 강제. 서버는 200 + JSON을 정상 응답하므로 네트워크 탭엔 200, 응답 본문도 보임.
- 응답에 `Access-Control-Allow-Origin` 헤더가 없어, 브라우저가 axios에게 본문 읽기를 차단 → CORS 오류.
- 백엔드에 CORS 설정 전무(grep 확인), Spring Security 의존성도 없음(web + data-jpa만).

## 변경 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `config/WebConfig.java` | + | `com.playstory.backend.config`(api·common과 동일 레벨). `/api/**` 에 origin `http://localhost:5173` 허용, 메서드 GET/POST/PUT/PATCH/DELETE/OPTIONS |

## 검증

- `./gradlew compileJava` → **BUILD SUCCESSFUL**.
- 재기동 전 현재 인스턴스 응답: `HTTP 200` + `Content-Type: application/json` 이나 `Access-Control-Allow-Origin` **없음** — 오류 원인 재확인.
- **적용에는 백엔드 재기동 필요**. 재기동 후 curl로 헤더 확인:

```
curl -i -H "Origin: http://localhost:5173" http://localhost:8080/api/excel-jobs
# → Access-Control-Allow-Origin: http://localhost:5173 포함
```

## 주의 · follow-up

- 현재 백엔드가 구버전 설정으로 실행 중 → **재기동해야 CORS 헤더가 적용**됨.
- 허용 origin은 로컬 `5173` 1개만. 배포 origin 추가 시 `allowedOrigins` 에 명시(과다 허용 금지).
- 커밋하지 않음(미요청).

> 후속 참고: 이후 same-origin 프록시(Vite dev proxy / nginx 리버스 프록시) 도입으로 CORS가 불필요해지며 `WebConfig.java` 는 삭제됨.
