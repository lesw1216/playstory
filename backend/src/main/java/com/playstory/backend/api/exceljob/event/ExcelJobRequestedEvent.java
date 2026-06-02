package com.playstory.backend.api.exceljob.event;

/**
 * 엑셀 생성 job이 PENDING으로 저장된 뒤 발행되는 이벤트.
 * 트랜잭션 commit 후(AFTER_COMMIT)에만 비동기 생성이 시작되도록 트리거 역할을 한다.
 */
public record ExcelJobRequestedEvent(Integer jobId) {
}
