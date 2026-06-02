package com.playstory.backend.api.exceljob.service;

import com.playstory.backend.api.exceljob.dto.query.ExcelJobSearchQuery;
import com.playstory.backend.api.exceljob.dto.response.ExcelJobPageResponse;
import com.playstory.backend.api.exceljob.dto.response.ExcelJobResponse;
import com.playstory.backend.api.exceljob.dto.result.ExcelJobResult;
import com.playstory.backend.api.exceljob.model.ExcelJob;
import com.playstory.backend.api.exceljob.repository.ExcelJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelJobService {

    private final ExcelJobRepository excelJobRepository;

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

}
