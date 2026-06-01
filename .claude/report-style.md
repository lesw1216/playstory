### 보고서 스타일 (엄수) — Tailwind CSS 기반

**단일 HTML 파일**: 스타일은 **Tailwind CSS** utility class로 작성한다. standalone 보고서이므로 Tailwind 는 **Play CDN** 으로 로드한다(파일 열람 시 인터넷 필요). 그 외 외부 CSS/이미지 의존성은 두지 않는다.

---

#### Tailwind 로드 & 설정

`<head>` 에 Play CDN script 1개와 인라인 설정만 둔다. 시스템 폰트 스택과 GitHub 계열 색 토큰을 테마로 등록하고, 다크 모드는 `media` 전략(`prefers-color-scheme`)으로 자동 전환한다.

```html
<script src="https://cdn.tailwindcss.com"></script>
<script>
  tailwind.config = {
    darkMode: 'media',
    theme: {
      extend: {
        fontFamily: {
          sans: ['-apple-system', 'BlinkMacSystemFont', '"Segoe UI"',
                 '"Apple SD Gothic Neo"', '"Malgun Gothic"', 'system-ui', 'sans-serif'],
          mono: ['ui-monospace', '"SF Mono"', 'Menlo', 'Consolas', 'monospace'],
        },
        colors: {
          // 라이트
          canvas: '#fafafa', card: '#ffffff', ink: '#1f2328', muted: '#656d76',
          link: '#0969da', line: '#d0d7de', ok: '#1a7f37', warn: '#9a6700',
          danger: '#cf222e', codebg: '#f6f8fa',
          // 다크 (dark: 접두어로 사용)
          'd-canvas': '#0d1117', 'd-card': '#161b22', 'd-ink': '#e6edf3',
          'd-muted': '#8b949e', 'd-link': '#58a6ff', 'd-line': '#30363d',
        },
      },
    },
  }
</script>
```

---

#### 레이아웃

- `<body>`: `bg-canvas text-ink font-sans dark:bg-d-canvas dark:text-d-ink`
- `<main>`: `max-w-[880px] mx-auto px-8 py-12`, 모바일은 `px-4 py-6` 로 좁힌다 — 좌우 가운데 정렬
- 본문 기준 `text-base leading-[1.7]`

#### 헤딩

- H1 `text-[28px] font-semibold`, H2 `text-[20px] font-semibold`, H3 `text-base font-semibold`
- 헤딩 아래 `border-bottom` 같은 장식선 금지 (카드 경계로 충분)

#### 섹션 카드

각 섹션은 `<section>` 카드로 감싼다.

```
bg-card dark:bg-d-card border border-line dark:border-d-line
rounded-lg p-6 mb-4
```

#### 최상단 헤더

제목(H1) + 타임스탬프(`font-mono text-sm text-muted dark:text-d-muted`) + 작업 요약 한 줄.

---

#### 변경 파일 목록

`<table>` 로 표현: `파일 경로 | 변경 유형 | 라인 변화 | 요약`.

- 테이블: `w-full border-collapse text-sm`
- 셀: `text-left px-2.5 py-2 border-b border-line dark:border-d-line align-top`
- 헤더 셀: `text-muted dark:text-d-muted font-semibold`
- 변경 유형은 색 배지(아래 규칙)로 표기

#### 배지

`inline-block rounded px-2 py-0.5 text-xs font-mono border` + 유형별 색:

- 추가 `+` : `text-ok border-ok`
- 수정 `~` : `text-link border-link`
- 삭제 `-` : `text-danger border-danger`

상태 표기는 ✓ / ✗ 기호만 허용한다.

#### 코드 블록

- 블록: `<pre>` 에 `bg-codebg dark:bg-d-card rounded-md p-4 overflow-x-auto text-sm font-mono`
- 인라인 코드: `bg-codebg dark:bg-d-card rounded px-1.5 py-0.5 text-sm font-mono`

#### 링크

`text-link dark:text-d-link`

---

#### 금지 사항

- 그라데이션 배경 금지
- 박스 그림자 금지 (`shadow-*` 사용 금지 — border로 구분)
- 이모지 금지 (상태 배지의 ✓ ✗ 기호는 허용)
- 애니메이션/transition 금지 (`transition-*`, `animate-*` 금지)
- 라운드 코너 `rounded-lg`(8px) 초과 금지
- 외부 CDN 폰트(Google Fonts 등) 로드 금지 — 시스템 폰트 스택만
- 인라인 `style=` 속성 금지 (모든 스타일은 Tailwind class로)
- 예외: 위 Tailwind Play CDN script 1개만 외부 로드를 허용한다

#### 접근성

- `<html lang="ko">` 명시
- 본문/배경 대비 WCAG AA 이상
- 시멘틱 태그 사용 (`<main>`, `<section>`, `<header>`, `<table>`, `<th>`)

---

#### 최소 골격

```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>{작업명}</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = { /* 위 설정 */ }
  </script>
</head>
<body class="bg-canvas text-ink font-sans dark:bg-d-canvas dark:text-d-ink">
  <main class="max-w-[880px] mx-auto px-8 py-12">
    <header class="mb-6">
      <h1 class="text-[28px] font-semibold">{작업명}</h1>
      <div class="font-mono text-sm text-muted dark:text-d-muted">{YYYY-MM-DD HH:mm}</div>
      <p class="text-muted dark:text-d-muted mt-2">{작업 요약 한 줄}</p>
    </header>

    <section class="bg-card dark:bg-d-card border border-line dark:border-d-line rounded-lg p-6 mb-4">
      <h2 class="text-[20px] font-semibold mb-4">{섹션 제목}</h2>
      <!-- ... -->
    </section>
  </main>
</body>
</html>
```
