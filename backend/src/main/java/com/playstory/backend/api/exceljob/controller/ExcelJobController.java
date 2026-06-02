package com.playstory.backend.api.exceljob.controller;

import com.playstory.backend.api.exceljob.dto.query.ExcelJobSearchQuery;
import com.playstory.backend.api.exceljob.dto.response.ExcelJobPageResponse;
import com.playstory.backend.api.exceljob.service.ExcelJobService;
import com.playstory.backend.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/excel-jobs")
@RequiredArgsConstructor
public class ExcelJobController {

    private final ExcelJobService excelJobService;

    /**
     * 엑셀 job 목록을 최신순으로 페이징 조회한다.
     *
     * @param page 페이지 번호 (0부터)
     * @param size 페이지 크기
     * @return 최신순 job 페이지 응답
     */
    @GetMapping
    public ResponseEntity<BaseResponse<ExcelJobPageResponse>> findAllLatest(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {

        ExcelJobSearchQuery query = ExcelJobSearchQuery.builder()
            .page(page)
            .size(size)
            .build();

        ExcelJobPageResponse excelJobPageResponse = excelJobService.findAllLatest(query);

        return ResponseEntity.ok(BaseResponse.success(excelJobPageResponse));
    }
}
