package com.playstory.backend.api.exceljob.repository;

import com.playstory.backend.api.exceljob.model.ExcelJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelJobRepository extends JpaRepository<ExcelJob, Integer> {

    /**
     * 요청 시각 기준 최신순으로 job 목록을 페이징 조회한다.
     *
     * @param pageable 페이지 번호·크기
     * @return 최신순 job 페이지
     */
    Page<ExcelJob> findAllByOrderByRequestedAtDesc(Pageable pageable);
}
