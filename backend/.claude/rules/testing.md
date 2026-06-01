# 테스트 작성 규칙

## 대상 및 범위

| 레이어 | 테스트 여부 | 도구 |
| ------ | ----- | ---- |
| Service | 필수 | JUnit 5 + Mockito |
| Controller | 필수 | MockMvc (`@WebMvcTest`) |
| Repository | 제외 | SQL은 통합 테스트 별도 판단 |

---

## 파일 위치

테스트 파일은 대상 클래스와 동일한 패키지 경로에 `Test` 접미사로 생성한다.

```
src/main/java/com/playstory/backend/api/exceljob/service/ExcelJobService.java
src/test/java/com/playstory/backend/api/exceljob/service/ExcelJobServiceTest.java
```

---

## 클래스 구조

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("ExcelJobService")
class ExcelJobServiceTest {

    @InjectMocks
    private ExcelJobService excelJobService;

    @Mock
    private ExcelJobRepository excelJobRepository;

    @Mock
    private OrderRepository orderRepository;
}
```

- `@ExtendWith(MockitoExtension.class)` 사용
- 클래스 `@DisplayName`은 테스트 대상 클래스명으로 작성

---

## 메서드 네이밍

메서드명은 **테스트 대상 상황을 즉시 파악할 수 있는 영어**로 작성한다.
`@DisplayName`에 한국어로 테스트 의도를 작성한다.

```java
// ✅ 올바른 예
@Test
@DisplayName("존재하지 않는 job ID로 조회하면 예외가 발생한다")
void findById_throwsException_whenJobNotFound() { ... }

@Test
@DisplayName("유효한 요청으로 job을 생성하면 PENDING 상태로 저장 후 응답을 반환한다")
void create_returnsPendingResponse_whenValidCommand() { ... }

@Test
@DisplayName("완료되지 않은 job을 다운로드하면 예외가 발생한다")
void download_throwsException_whenJobNotDone() { ... }

// 잘못된 예
void test1() { ... }
void job생성테스트() { ... }
void createJob() { ... }  // 상황 정보 없음
```

메서드명 패턴: `{대상메서드}_{결과}_{조건}` (조건이 명확할 때만 조건 추가)

---

## Given / When / Then 구조

모든 테스트 메서드는 `// given`, `// when`, `// then` 주석으로 블록을 구분한다.
첫 줄은 빈 줄로 시작한다 (`@rules/coding-convention.md` 준수).

```java
@Test
@DisplayName("유효한 요청으로 job을 생성하면 PENDING 상태로 저장 후 응답을 반환한다")
void create_returnsPendingResponse_whenValidCommand() {

    // given
    ExcelJobCreateCommand command = ExcelJobCreateCommand.builder()
        .targetTable("order")
        .startDate(LocalDate.of(2026, 1, 1))
        .endDate(LocalDate.of(2026, 1, 31))
        .build();

    // when
    ExcelJobResponse response = excelJobService.create(command);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo("PENDING");
    then(excelJobRepository).should().save(any(ExcelJob.class));
}
```

---

## 예외 검증

예외 발생 케이스는 `assertThatThrownBy`로 검증한다.

```java
@Test
@DisplayName("존재하지 않는 job ID로 다운로드하면 예외가 발생한다")
void download_throwsException_whenJobNotFound() {

    // given
    given(excelJobRepository.findById(999L)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> excelJobService.download(999L))
        .isInstanceOf(BaseException.class)
        .hasMessageContaining(BaseResponseStatus.EXCEL_JOB_NOT_FOUND.getMessage());
}
```

---

## Mockito 스타일

BDD 스타일을 사용한다.

```java
// BDD 스타일
given(excelJobRepository.findById(id)).willReturn(Optional.of(job));
then(excelJobRepository).should().save(any());
then(excelJobRepository).should(never()).delete(any());

// classic 스타일 (사용 금지)
when(excelJobRepository.findById(id)).thenReturn(Optional.of(job));
verify(excelJobRepository).save(any());
```

---

## Assertion 스타일

`AssertJ`를 사용한다. JUnit의 `assertEquals` 직접 사용 금지.

```java
// AssertJ
assertThat(response.getStatus()).isEqualTo("PENDING");
assertThat(jobs).hasSize(3).extracting("status").contains("DONE");

// JUnit assertions (사용 금지)
assertEquals("PENDING", response.getStatus());
```

---

## 공통 규칙 요약

| 항목 | 규칙 |
| ---- | ---- |
| 테스트 프레임워크 | JUnit 5 + Mockito + AssertJ |
| 클래스 어노테이션 | `@ExtendWith(MockitoExtension.class)` |
| 메서드명 | 영어, 상황을 즉시 파악 가능한 서술형 |
| DisplayName | 한국어, 테스트 의도 명확히 서술 |
| 구조 | given / when / then 주석 필수 |
| Mock 스타일 | BDD (`given`, `then`) |
| Assertion | AssertJ (`assertThat`) |
| 예외 검증 | `assertThatThrownBy` |
