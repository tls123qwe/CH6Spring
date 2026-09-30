package com.example.ch6spring.domain.menu.repository;

import com.example.ch6spring.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
