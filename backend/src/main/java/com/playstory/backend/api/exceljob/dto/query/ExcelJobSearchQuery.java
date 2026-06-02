package com.playstory.backend.api.exceljob.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelJobSearchQuery {

    private final int page;
    private final int size;
    // 추후 status / 날짜 필터 확장 여지
}
