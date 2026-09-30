package com.example.ch6spring.domain.point.dto;

import com.example.ch6spring.domain.user.entity.User;

public record PointChargeResponse(
        Long userId,
        long point
) {

    public static PointChargeResponse from(User user) {

        return new PointChargeResponse(user.getId(), user.getPoint());
    }
}