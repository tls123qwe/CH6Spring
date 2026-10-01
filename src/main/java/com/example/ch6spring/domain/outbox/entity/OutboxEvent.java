package com.example.ch6spring.domain.outbox.entity;

import com.example.ch6spring.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event",
        // 스케줄러가 "PENDING + 오래된 순"으로 조회하므로 인덱스 추가
        indexes = @Index(name = "idx_outbox_status_created", columnList = "status, created_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 주문과 같은 트랜잭션에 저장
// 주문이 커밋되면 반드시 남고, 롤백 되면 데이터도 사라진다.
public class OutboxEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 주문의 이벤트인지 확인을 위해 ID만 저장
    @Column(nullable = false)
    private Long orderId;

    // 전송할 데이터를 JSON 문자열로 저장
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    // 전송 실패 카운트
    @Column(nullable = false)
    private int retryCount;

    // 전송 성공 시간
    private LocalDateTime sentAt;

    // 처음 만들어질 때는 PENDING + 0
    public OutboxEvent(Long orderId, String payload) {

        this.orderId = orderId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
    }

    public static OutboxEvent create(Long orderId, String payload) {

        return new OutboxEvent(orderId, payload);
    }

    // 전송 성공 처리
    public void markSent() {

        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    // 전송 실패 처리 시 횟수를 올리고, 최대 횟수에 도달하면 FAILED
    public void recordFailure(int maxRetry) {

        this.retryCount++;
        if (this.retryCount >= maxRetry) this.status = OutboxStatus.FAILED;
    }
}