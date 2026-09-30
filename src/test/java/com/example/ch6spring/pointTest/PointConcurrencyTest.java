package com.example.ch6spring.pointTest;

import com.example.ch6spring.domain.point.repository.PointHistoryRepository;
import com.example.ch6spring.domain.point.service.PointService;
import com.example.ch6spring.domain.user.entity.User;
import com.example.ch6spring.domain.user.repository.UserRepository;
import org.apache.kafka.common.errors.InterruptException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PointConcurrencyTest {

    @Autowired PointService pointService;
    @Autowired UserRepository userRepository;
    @Autowired PointHistoryRepository pointHistoryRepository;

    @AfterEach
    void tearDown() {
        pointHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 같은_사용자가_동시에_100번_충전해도_잔액이_정확하다() throws InterruptedException {
        // given
        User user = userRepository.save(User.create("테스터"));
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.charge(user.getId(), 100);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        User result = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.getPoint()).isEqualTo(10_000);
        assertThat(pointHistoryRepository.count()).isEqualTo(100);
    }
}