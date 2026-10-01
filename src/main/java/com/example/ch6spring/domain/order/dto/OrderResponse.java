package com.example.ch6spring.domain.order.dto;

import com.example.ch6spring.domain.order.entity.Order;
import com.example.ch6spring.domain.user.entity.User;

public record OrderResponse(
        Long orderId,
        Long menuId,
        int price,
        long remainingPoint
) {

    public static OrderResponse of(Order order, User user) {

        return new OrderResponse(
                order.getId(),
                order.getMenu().getId(),
                order.getPrice(),
                user.getPoint());
    }
}