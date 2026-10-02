package com.example.ch6spring.domain.outbox.entity;

// 이벤트 전송 상태
public enum OutboxStatus {

    PENDING,    // 전송 대기 OR 재시도 대기
    SENT,       // 전송 완료
    FAILED      // 최대 재시도 횟수 초과
}
