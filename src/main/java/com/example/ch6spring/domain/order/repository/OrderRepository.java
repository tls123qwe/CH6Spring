package com.example.ch6spring.domain.order.repository;

import com.example.ch6spring.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
