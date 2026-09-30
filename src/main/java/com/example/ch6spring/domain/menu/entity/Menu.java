package com.example.ch6spring.domain.menu.entity;

import com.example.ch6spring.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    private Menu(String name, int price) {

        this.name = name;
        this.price = price;
    }

    public static Menu create(String name, int price) {

        return new Menu(name, price);
    }
}