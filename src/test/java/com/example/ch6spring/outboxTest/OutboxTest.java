package com.example.ch6spring.outboxTest;

import com.example.ch6spring.domain.menu.entity.Menu;
import com.example.ch6spring.domain.menu.repository.MenuRepository;
import com.example.ch6spring.domain.order.repository.OrderRepository;
import com.example.ch6spring.domain.order.service.OrderService;
import com.example.ch6spring.domain.outbox.client.DataPlatformClient;
import com.example.ch6spring.domain.outbox.entity.OutboxEvent;
import com.example.ch6spring.domain.outbox.entity.OutboxStatus;
import com.example.ch6spring.domain.outbox.repository.OutboxEventRepository;
import com.example.ch6spring.domain.outbox.service.OutboxSender;
import com.example.ch6spring.domain.point.repository.PointHistoryRepository;
import com.example.ch6spring.domain.user.entity.User;
import com.example.ch6spring.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest

// 주문하면 외부로 전송되는지
// 외부가 망가져도 주문은 성공되는지
// 장애가 복구되면 재전송되는지
// 주문이 실패하면 이벤트도 남지 않는지
public class OutboxTest {

    @Autowired OrderService orderService;
    @Autowired OutboxSender outboxSender;
    @Autowired UserRepository userRepository;
    @Autowired MenuRepository menuRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired PointHistoryRepository pointHistoryRepository;
    @Autowired OutboxEventRepository outboxEventRepository;

    // 진짜 MockDataPlatformClient 대신 가짜를 끼워 넣는다.
    // 테스트에서 성공/실패를 마음대로 조작하고, 호출 여부를 검증할 수 있다.
    @MockitoBean
    DataPlatformClient dataPlatformClient;

    // 테스트끼리 데이터가 섞이지 않도록 매번 정리
    // (주문이 사용자/메뉴를 참조하므로 참조하는 쪽부터 삭제)
    @AfterEach
    void tearDown() {
        outboxEventRepository.deleteAll();
        orderRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();
    }

    // 테스트용 사용자 생성 (포인트 충전까지)
    private Long createUser(long point) {
        User user = User.create("테스터");
        user.chargePoint(point);
        return userRepository.save(user).getId();
    }

    // 테스트용 메뉴 생성
    private Long createMenu(int price) {
        return menuRepository.save(Menu.create("테스트커피", price)).getId();
    }

    @Test
    void 주문하면_데이터_플랫폼으로_전송되고_SENT가_된다() {
        // given
        Long userId = createUser(1000);
        Long menuId = createMenu(100);

        // when
        orderService.order(userId, menuId);

        // then
        // 전송은 비동기(@Async)라 언제 끝날지 모른다.
        // 최대 3초 동안 조건이 맞을 때까지 반복 확인한다. (Thread.sleep보다 빠르고 안정적)
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            OutboxEvent event = outboxEventRepository.findAll().get(0);
            assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
        });

        // 요구사항의 전송 데이터(userId, menuId, 결제금액)가 정확히 전달됐는지 확인
        verify(dataPlatformClient).send(argThat(p ->
                p.userId().equals(userId)
                        && p.menuId().equals(menuId)
                        && p.paymentAmount() == 100));
    }

    @Test
    void 외부_플랫폼이_장애여도_주문은_성공하고_PENDING으로_남는다() {
        // given
        // 외부 플랫폼이 항상 실패하도록 설정 (장애 상황 연출)
        doThrow(new RuntimeException("플랫폼 장애")).when(dataPlatformClient).send(any());
        Long userId = createUser(1000);
        Long menuId = createMenu(100);

        // when
        orderService.order(userId, menuId);

        // then
        // 전송은 실패했지만 이벤트는 사라지지 않고 PENDING으로 남아 재전송을 기다린다
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            OutboxEvent event = outboxEventRepository.findAll().get(0);
            assertThat(event.getRetryCount()).isEqualTo(1);
            assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        });

        // 핵심: 외부가 망가져도 주문은 정상적으로 저장되어 있다
        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void 장애가_복구되면_재전송으로_SENT가_된다() {
        // given
        // 1. 처음엔 장애 상태에서 주문
        doThrow(new RuntimeException("플랫폼 장애")).when(dataPlatformClient).send(any());
        Long userId = createUser(1000);
        Long menuId = createMenu(100);
        orderService.order(userId, menuId);

        // 즉시 전송이 실패할 때까지 대기
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(outboxEventRepository.findAll().get(0).getRetryCount()).isEqualTo(1));

        // 2. 장애 복구: 이제 정상적으로 전송되도록 변경
        doNothing().when(dataPlatformClient).send(any());
        Long eventId = outboxEventRepository.findAll().get(0).getId();

        // when
        // 스케줄러를 10초 기다리는 대신, 재전송을 직접 호출해서 흉내 낸다
        outboxSender.sendById(eventId);

        // then
        OutboxEvent event = outboxEventRepository.findById(eventId).orElseThrow();
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    void 주문이_실패하면_Outbox도_저장되지_않는다() {
        // given
        // 잔액(50P)보다 비싼 메뉴(100P) → 잔액 부족으로 주문 실패
        Long userId = createUser(50);
        Long menuId = createMenu(100);

        // when
        assertThatThrownBy(() -> orderService.order(userId, menuId));

        // then
        // Outbox 패턴의 핵심 약속: 주문이 롤백되면 "보낼 데이터"도 함께 롤백된다
        // → 존재하지 않는 주문이 외부로 전송되는 일이 없다
        assertThat(outboxEventRepository.count()).isZero();
    }
}