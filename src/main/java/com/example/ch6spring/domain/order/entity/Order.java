package com.example.ch6spring.domain.order.entity;

import com.example.ch6spring.common.entity.BaseTimeEntity;
import com.example.ch6spring.domain.menu.entity.Menu;
import com.example.ch6spring.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int price;

    private Order(User user, Menu menu) {
        this.user = user;
        this.menu = menu;
        this.price = menu.getPrice();
    }

    public static Order create(User user, Menu menu) {

        return new Order(user, menu);
    }
}