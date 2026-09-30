package com.example.ch6spring.domain.menu.dto;

import com.example.ch6spring.domain.menu.entity.Menu;

public record MenuResponse(
        Long menuId,
        String name,
        int price
) {

    public static MenuResponse from(Menu menu) {

        return new MenuResponse(menu.getId(), menu.getName(), menu.getPrice());
    }
}