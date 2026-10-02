package com.example.ch6spring.domain.menu.controller;

import com.example.ch6spring.domain.menu.dto.MenuResponse;
import com.example.ch6spring.domain.menu.dto.PopularMenuResponse;
import com.example.ch6spring.domain.menu.service.MenuService;
import com.example.ch6spring.domain.menu.service.PopularMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")

public class MenuController {

    private final MenuService menuService;
    private final PopularMenuService popularMenuService;

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus() {

        return ResponseEntity.ok(menuService.getMenus());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularMenuResponse>> getPopularMenus() {

        return ResponseEntity.ok(popularMenuService.getPopularMenus());
    }
}