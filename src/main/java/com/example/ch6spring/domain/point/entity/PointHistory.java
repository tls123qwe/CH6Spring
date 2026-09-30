package com.example.ch6spring.domain.point.entity;

import com.example.ch6spring.common.entity.BaseTimeEntity;
import com.example.ch6spring.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    @Column(nullable = false)
    private long balanceAfter;

    private PointHistory(User user, long amount, PointType type) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.balanceAfter = user.getPoint();
    }

    public static PointHistory charge(User user, long amount) {

        return new PointHistory(user, amount, PointType.CHARGE);
    }

    public static PointHistory use(User user, long amount) {

        return new PointHistory(user, amount, PointType.USE);
    }
}
