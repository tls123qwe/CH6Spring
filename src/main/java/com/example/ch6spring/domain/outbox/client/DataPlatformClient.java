package com.example.ch6spring.domain.outbox.client;

// 데이터 수집 플랫폼과 통신하는 인터페이스
// 실제 플랫폼이 생가면 구현체만 교체하면 사용 가능
public interface DataPlatformClient {

    void send(OrderDataPayload payload);
}