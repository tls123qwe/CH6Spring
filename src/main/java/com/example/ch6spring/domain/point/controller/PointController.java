package com.example.ch6spring.domain.point.controller;

import com.example.ch6spring.domain.point.dto.PointChargeRequest;
import com.example.ch6spring.domain.point.dto.PointChargeResponse;
import com.example.ch6spring.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/points")

public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<PointChargeResponse> charge(
            @PathVariable Long userId,
            @RequestBody @Valid PointChargeRequest request) {

        return ResponseEntity.ok(pointService.charge(userId, request.amount()));
    }
}