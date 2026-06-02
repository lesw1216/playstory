package com.playstory.backend.api.exceljob.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class ExcelJobPageResponse {

    private final List<ExcelJobResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    /**
     * 응답으로 변환된 페이지에서 목록과 페이징 메타 정보를 추출해 페이지 응답을 생성한다.
     *
     * @param page 이미 ExcelJobResponse로 매핑된 페이지
     * @return 페이지 응답
     */
    public static ExcelJobPageResponse from(Page<ExcelJobResponse> page) {

        return ExcelJobPageResponse.builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
