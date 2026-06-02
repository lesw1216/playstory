package com.playstory.backend.api.exceljob.service;

import com.playstory.backend.api.exceljob.event.ExcelJobRequestedEvent;
import com.playstory.backend.api.exceljob.excel.OrderExcelWriter;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 엑셀 생성 비동기 워커. PENDING job 트랜잭션 commit 후(AFTER_COMMIT) 전용 스레드풀에서 실행되어
 * PROCESSING → DONE/FAILED 로 상태를 전이시킨다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelGenerator {

    private final ExcelJobService excelJobService;
    private final OrderExcelWriter orderExcelWriter;

    /**
     * 엑셀 생성 요청을 비동기로 처리한다. 트랜잭션 commit된 PENDING job만 수신하므로 조회 race가 없다.
     *
     * @param event 생성 대상 jobId를 담은 이벤트
     */
    @Async("excelExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onExcelJobRequested(ExcelJobRequestedEvent event) {

        Integer jobId = event.jobId();
        excelJobService.markProcessing(jobId);

        try {
            Path file = orderExcelWriter.write(jobId);
            excelJobService.markDone(jobId, file.toString());
        } catch (Exception e) {
            log.error("엑셀 생성 실패 jobId={}", jobId, e);
            excelJobService.markFailed(jobId);
        }
    }
}
