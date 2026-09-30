package com.example.ch6spring.domain.point.service;

import com.example.ch6spring.common.exception.BusinessException;
import com.example.ch6spring.common.exception.ErrorCode;
import com.example.ch6spring.domain.point.dto.PointChargeResponse;
import com.example.ch6spring.domain.point.entity.PointHistory;
import com.example.ch6spring.domain.point.repository.PointHistoryRepository;
import com.example.ch6spring.domain.user.entity.User;
import com.example.ch6spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class PointService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointChargeResponse charge(Long userId, long amount) {

        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.chargePoint(amount);
        pointHistoryRepository.save(PointHistory.charge(user, amount));

        return PointChargeResponse.from(user);
    }
}
