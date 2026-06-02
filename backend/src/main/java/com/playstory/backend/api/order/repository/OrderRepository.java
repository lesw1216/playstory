package com.playstory.backend.api.order.repository;

import com.playstory.backend.api.order.model.Order;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * id 기준 keyset 페이징으로 주문을 청크 조회한다.
     * deep-offset 비용 없이 대용량(10만건)을 순차 스트리밍하기 위해 사용한다.
     *
     * @param id 직전 청크의 마지막 id (첫 청크는 0)
     * @param pageable 청크 크기 (정렬은 메서드명이 보장하므로 size만 의미)
     * @return id가 큰 순서로 정렬된 다음 청크
     */
    List<Order> findByIdGreaterThanOrderByIdAsc(Integer id, Pageable pageable);
}
