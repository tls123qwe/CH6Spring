package com.example.ch6spring.domain.order.repository;

import com.example.ch6spring.domain.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select o.menu.id as menuId, o.menu.name as name, count(o) as orderCount
            from Order o
            where o.createdAt >= :from
            group by o.menu.id, o.menu.name
            order by count(o) desc, o.menu.id asc
            """)
    List<PopularMenuProjection> findPopularMenus(@Param("from") LocalDateTime from, Pageable pageable);
}
