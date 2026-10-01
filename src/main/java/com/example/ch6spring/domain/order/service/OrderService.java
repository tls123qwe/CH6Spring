package com.example.ch6spring.domain.order.service;

import com.example.ch6spring.common.exception.BusinessException;
import com.example.ch6spring.common.exception.ErrorCode;
import com.example.ch6spring.domain.menu.entity.Menu;
import com.example.ch6spring.domain.menu.repository.MenuRepository;
import com.example.ch6spring.domain.order.dto.OrderResponse;
import com.example.ch6spring.domain.order.entity.Order;
import com.example.ch6spring.domain.order.repository.OrderRepository;
import com.example.ch6spring.domain.point.entity.PointHistory;
import com.example.ch6spring.domain.point.repository.PointHistoryRepository;
import com.example.ch6spring.domain.user.entity.User;
import com.example.ch6spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public OrderResponse order(Long userId, Long menuId) {

        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        user.usePoint(menu.getPrice());
        pointHistoryRepository.save(PointHistory.use(user, menu.getPrice()));

        Order order = orderRepository.save(Order.create(user, menu));

        return OrderResponse.of(order, user);
    }
}
