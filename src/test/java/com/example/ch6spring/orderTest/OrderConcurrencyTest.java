package com.example.ch6spring.orderTest;

import com.example.ch6spring.domain.menu.entity.Menu;
import com.example.ch6spring.domain.menu.repository.MenuRepository;
import com.example.ch6spring.domain.order.repository.OrderRepository;
import com.example.ch6spring.domain.order.service.OrderService;
import com.example.ch6spring.domain.point.repository.PointHistoryRepository;
import com.example.ch6spring.domain.user.entity.User;
import com.example.ch6spring.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderConcurrencyTest {

    @Autowired OrderService orderService;
    @Autowired UserRepository userRepository;
    @Autowired MenuRepository menuRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired PointHistoryRepository pointHistoryRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void 잔액보다_많은_주문이_동시에_들어오면_잔액만큼만_성공한다() throws InterruptedException {
        // given
        User user = User.create("테스터");
        user.chargePoint(500);
        Long userId = userRepository.save(user).getId();
        Long menuId = menuRepository.save(Menu.create("테스트커피", 100)).getId();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderService.order(userId, menuId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        User result = userRepository.findById(userId).orElseThrow();
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(5);
        assertThat(result.getPoint()).isZero();
        assertThat(orderRepository.count()).isEqualTo(5);
    }
}
