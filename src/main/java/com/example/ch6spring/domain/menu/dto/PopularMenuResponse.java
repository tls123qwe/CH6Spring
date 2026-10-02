package com.example.ch6spring.domain.menu.dto;

import com.example.ch6spring.domain.order.repository.PopularMenuProjection;

import java.io.Serializable;

public record PopularMenuResponse(
        int rank,
        Long menuId,
        String name,
        long orderCount
) implements Serializable {

    public static PopularMenuResponse of(int rank, PopularMenuProjection projection) {

        return new PopularMenuResponse(
                rank,
                projection.getMenuId(),
                projection.getName(),
                projection.getOrderCount()
        );
    }
}