package com.playstory.backend.api.exceljob.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 엑셀 생성 비동기 작업. {@code excel_export_jobs} 테이블에 매핑된다.
 * 상태 전이는 행위 메서드로만 수행한다 (Rich Domain Model).
 */
@Entity
@Table(name = "excel_export_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExcelJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "started_at", nullable = true)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = true)
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ExcelJobStatus status;

    @Column(name = "file_path", nullable = true, length = 200)
    private String filePath;

    /**
     * 요청 시점의 PENDING job을 생성한다.
     *
     * @return PENDING 상태의 새 job
     */
    public static ExcelJob pending() {

        return ExcelJob.builder()
            .status(ExcelJobStatus.PENDING)
            .requestedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 작업을 시작 상태(PROCESSING)로 전이하고 시작 시각을 기록한다.
     */
    public void startProcessing() {

        this.status = ExcelJobStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * 작업을 완료 상태(DONE)로 전이하고 종료 시각과 파일 경로를 기록한다.
     *
     * @param filePath 생성된 엑셀 파일의 저장 경로
     */
    public void complete(String filePath) {

        this.status = ExcelJobStatus.DONE;
        this.endedAt = LocalDateTime.now();
        this.filePath = filePath;
    }

    /**
     * 작업을 실패 상태(FAILED)로 전이하고 종료 시각을 기록한다.
     */
    public void fail() {

        this.status = ExcelJobStatus.FAILED;
        this.endedAt = LocalDateTime.now();
    }
}
