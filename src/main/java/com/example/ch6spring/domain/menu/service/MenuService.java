package com.example.ch6spring.domain.menu.service;

import com.example.ch6spring.domain.menu.dto.MenuResponse;
import com.example.ch6spring.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> getMenus() {

        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .toList();
    }
}