package com.example.ch6spring.domain.order.repository;

public interface PopularMenuProjection {

    Long getMenuId();
    String getName();
    Long getOrderCount();
}
