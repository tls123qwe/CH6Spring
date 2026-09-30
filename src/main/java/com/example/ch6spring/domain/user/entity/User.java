package com.example.ch6spring.domain.user.entity;

import com.example.ch6spring.common.entity.BaseTimeEntity;
import com.example.ch6spring.common.exception.BusinessException;
import com.example.ch6spring.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long point;

    private User(String name) {

        this.name = name;
        this.point = 0;
    }

    public static User create(String name) {

        return new User(name);
    }

    // 포인트 충전
    public void chargePoint(long amount) {

        validateAmount(amount);
        this.point += amount;
    }

    // 포인트 사용
    public void usePoint(long amount) {

        validateAmount(amount);
        if (this.point < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }
        this.point -= amount;
    }

    // 금액 검증
    public void validateAmount(long amount) {

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }
}