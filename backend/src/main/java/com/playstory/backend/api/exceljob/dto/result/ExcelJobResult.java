package com.playstory.backend.api.exceljob.dto.result;

import com.playstory.backend.api.exceljob.model.ExcelJob;
import com.playstory.backend.api.exceljob.model.ExcelJobStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelJobResult {

    private final Integer jobId;
    private final ExcelJobStatus status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String filePath;

    /**
     * 엑셀 job 엔티티를 Repository 반환용 Result로 변환한다.
     *
     * @param job 변환할 엔티티
     * @return 변환된 Result
     */
    public static ExcelJobResult from(ExcelJob job) {

        return ExcelJobResult.builder()
            .jobId(job.getId())
            .status(job.getStatus())
            .requestedAt(job.getRequestedAt())
            .startedAt(job.getStartedAt())
            .endedAt(job.getEndedAt())
            .filePath(job.getFilePath())
            .build();
    }
}
