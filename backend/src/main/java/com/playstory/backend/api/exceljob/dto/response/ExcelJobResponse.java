package com.playstory.backend.api.exceljob.dto.response;

import com.playstory.backend.api.exceljob.dto.result.ExcelJobResult;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelJobResponse {

    private final Integer jobId;
    private final String status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String filePath;

    /**
     * Result를 클라이언트 응답용 Response로 변환한다.
     *
     * @param result 변환할 Result
     * @return 변환된 Response
     */
    public static ExcelJobResponse from(ExcelJobResult result) {

        return ExcelJobResponse.builder()
            .jobId(result.getJobId())
            .status(result.getStatus().name())
            .requestedAt(result.getRequestedAt())
            .startedAt(result.getStartedAt())
            .endedAt(result.getEndedAt())
            .filePath(result.getFilePath())
            .build();
    }
}
