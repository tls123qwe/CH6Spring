package com.example.ch6spring.domain.outbox.client;

import com.example.ch6spring.domain.order.entity.Order;

// 데이터 수집 플랫폼으로 보낼 주문 데이터.
public record OrderDataPayload(
        Long orderId,
        Long userId,
        Long menuId,
        int paymentAmount
) {

    public static OrderDataPayload from(Order order) {

        return new OrderDataPayload(
                order.getId(),
                order.getUser().getId(),
                order.getMenu().getId(),
                order.getPrice()
        );
    }
}
