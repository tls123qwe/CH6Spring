package com.example.ch6spring.domain.menu.service;

import com.example.ch6spring.domain.menu.dto.PopularMenuResponse;
import com.example.ch6spring.domain.order.repository.OrderRepository;
import com.example.ch6spring.domain.order.repository.PopularMenuProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class PopularMenuService {

    private static final int POPULAR_DAYS = 7;
    private static final int TOP_COUNT = 3;

    private final OrderRepository orderRepository;

    @Cacheable(cacheNames = "popularMenus", key = "'top3'")
    public List<PopularMenuResponse> getPopularMenus() {

        LocalDateTime from = LocalDateTime.now().minusDays(POPULAR_DAYS);
        List<PopularMenuProjection> result =
                orderRepository.findPopularMenus(from, PageRequest.of(0, TOP_COUNT));

        return IntStream.range(0, result.size())
                .mapToObj(i-> PopularMenuResponse.of(i + 1, result.get(i)))
                .toList();
    }
}