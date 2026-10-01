package com.example.ch6spring.domain.order.controller;

import com.example.ch6spring.domain.order.dto.OrderRequest;
import com.example.ch6spring.domain.order.dto.OrderResponse;
import com.example.ch6spring.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody @Valid OrderRequest request) {

        OrderResponse response = orderService.order(request.userId(), request.menuId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}