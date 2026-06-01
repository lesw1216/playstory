# 환경 변수 규칙

보안이 필요한 설정값은 코드에 하드코딩하지 않고 `src/main/resources/.env` 에 작성한다.

---

## 파일 위치

```
src/main/resources/
├── .env          ← 실제 값 (git 제외, .gitignore 등록됨)
├── .env.example  ← 키 목록만 작성한 템플릿 (git 포함)
└── application.yaml
```

---

## .env 작성 형식

```properties
DB_URL=jdbc:mysql://localhost:3306/playstory
DB_USERNAME=root
DB_PASSWORD=1234
EXCEL_OUTPUT_DIR=/data/excel
```

---

## .env.example 작성 형식

실제 값 없이 키와 설명만 작성한다. 새 환경 변수 추가 시 반드시 함께 업데이트한다.

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=
EXCEL_OUTPUT_DIR=
```

---

## application.yaml 참조 방법

`${KEY}` 형식으로 참조한다.

```yaml
spring:
  config:
    import: optional:file:src/main/resources/.env[.properties]
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

excel:
  output-dir: ${EXCEL_OUTPUT_DIR}
```

`spring.config.import` 를 통해 `.env` 파일을 로드한다.
`.env` 파일이 없어도 실행이 중단되지 않도록 `optional:` 접두사를 붙인다.

---

## 규칙 요약

- DB 접속 정보, 파일 저장 경로, 외부 API 키 등 보안·환경 의존 값은 반드시 `.env` 에 작성
- `application.yaml` 에 값을 직접 작성 금지 → `${KEY}` 로만 참조
- `.env` 는 git에 포함하지 않는다 (`.gitignore` 등록 확인)
- `.env.example` 은 반드시 git에 포함한다
- 새 환경 변수 추가 시 `.env`, `.env.example`, `application.yaml` 세 파일을 함께 수정한다
