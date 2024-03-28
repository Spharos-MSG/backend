package spharos.msg.domain.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import spharos.msg.domain.orders.dto.OrderRequest.OrderDto;
import spharos.msg.domain.orders.dto.OrderResponse.OrderUserDto;
import spharos.msg.domain.orders.entity.Orders;
import spharos.msg.domain.orders.repository.OrderRepository;
import spharos.msg.domain.users.entity.Address;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.AddressRepository;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.exception.OrderException;

@DataJpaTest
@Import(OrderService.class)
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    OrderService orderService;

    @BeforeEach
    public void init() {
        orderRepository.save(
            Orders.builder()
                .address("abc")
                .buyerId(1L)
                .buyerName("test")
                .buyerPhoneNumber("01012345667")
                .totalAmount(10L)
                .build());

        Address address = Address.builder()
            .addressName("부산 남구")
            .mobileNumber("01092312316")
            .recipient("홍준표")
            .addressName("부산 남구 용소로")
            .addressPhoneNumber("01092312316")
            .address("부산남구용")
            .build();
        addressRepository.save(address);
        usersRepository.save(
            Users.builder()
                .userName("test")
                .email("tjdvy963@naver.com")
                .loginId("abcdsd")
                .uuid("uuid")
                .password("1234")
                .phoneNumber("01092312316")
                .build()
        );
    }

    @Test
    @DisplayName("uuid를 찾을 수 없다면 OrderException이 발생한다.")
    void 유저_예외_발생_테스트() {
        //given
        OrderDto orderDto = new OrderDto();
        //when
        assertThatThrownBy(
            () -> orderService.saveOrder(List.of(orderDto), "s"))
            .isInstanceOf(OrderException.class);
        //then
    }

    @Test
    @DisplayName("주문자 정보 조회시 uuid에 해당하는 유저가 없다면 OrderException 발생")
    void 유저_없음_예외_테스트() {
        assertThatThrownBy(
            () -> orderService.findOrderUser("no"))
            .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("주문자 정보 조회시 모든 정보가 일치해야한다.")
    void 주문자_정보_조회_성공_테스트() {
        OrderUserDto orderUserDto = orderService.findOrderUser("uuid");
        assertThat(orderUserDto.toString()).contains(
            "uuid", "tjdvy963@naver.com", "abcdsd", "01092312316");
    }
}