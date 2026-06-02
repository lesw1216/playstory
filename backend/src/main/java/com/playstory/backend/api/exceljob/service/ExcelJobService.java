package com.playstory.backend.api.exceljob.service;

import com.playstory.backend.api.exceljob.dto.query.ExcelJobSearchQuery;
import com.playstory.backend.api.exceljob.dto.response.ExcelJobPageResponse;
import com.playstory.backend.api.exceljob.dto.response.ExcelJobResponse;
import com.playstory.backend.api.exceljob.dto.result.ExcelJobResult;
import com.playstory.backend.api.exceljob.event.ExcelJobRequestedEvent;
import com.playstory.backend.api.exceljob.model.ExcelJob;
import com.playstory.backend.api.exceljob.repository.ExcelJobRepository;
import com.playstory.backend.common.BaseException;
import com.playstory.backend.common.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExcelJobService {

    private final ExcelJobRepository excelJobRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 엑셀 job 목록을 최신순으로 페이징 조회한다.
     *
     * @param query 페이지 번호·크기 조회 조건
     * @return 최신순 job 페이지 응답
     */
    public ExcelJobPageResponse findAllLatest(ExcelJobSearchQuery query) {

        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<ExcelJob> excelJobs = excelJobRepository.findAllByOrderByRequestedAtDesc(pageable);

        return ExcelJobPageResponse.from(
                excelJobs.map(excelJob -> ExcelJobResponse.from(ExcelJobResult.from(excelJob)))
        );
    }

    /**
     * PENDING 상태의 엑셀 job을 생성하고, 트랜잭션 commit 후 비동기 생성이 시작되도록 이벤트를 발행한다.
     *
     * @return 생성된 job 응답 (초기 상태 PENDING)
     */
    @Transactional
    public ExcelJobResponse create() {

        ExcelJob job = excelJobRepository.save(ExcelJob.pending());

        eventPublisher.publishEvent(new ExcelJobRequestedEvent(job.getId()));

        return ExcelJobResponse.from(ExcelJobResult.from(job));
    }

    /**
     * job을 PROCESSING 상태로 전이한다.
     *
     * @param jobId 대상 job ID
     * @throws BaseException job이 존재하지 않을 때
     */
    @Transactional
    public void markProcessing(Integer jobId) {

        findJob(jobId).startProcessing();
    }

    /**
     * job을 DONE 상태로 전이하고 파일 경로를 기록한다.
     *
     * @param jobId 대상 job ID
     * @param filePath 생성된 엑셀 파일 경로
     * @throws BaseException job이 존재하지 않을 때
     */
    @Transactional
    public void markDone(Integer jobId, String filePath) {

        findJob(jobId).complete(filePath);
    }

    /**
     * job을 FAILED 상태로 전이한다.
     *
     * @param jobId 대상 job ID
     * @throws BaseException job이 존재하지 않을 때
     */
    @Transactional
    public void markFailed(Integer jobId) {

        findJob(jobId).fail();
    }

    private ExcelJob findJob(Integer jobId) {

        return excelJobRepository.findById(jobId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.EXCEL_JOB_NOT_FOUND));
    }
}
