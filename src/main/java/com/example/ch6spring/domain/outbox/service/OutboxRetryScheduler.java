package com.example.ch6spring.domain.outbox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

// 즉시 전송에 실패했거나 누락된 이벤트를 주기적으로 다시 보냄(신뢰성)
public class OutboxRetryScheduler {

    private final OutboxSender outboxSender;

    // 이전 실행이 끝난 후 10초 뒤에 다시 실행
    @Scheduled(fixedDelay = 10_000)
    public void retry() {

        outboxSender.retryPending();
    }
}