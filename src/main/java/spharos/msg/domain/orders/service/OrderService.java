package spharos.msg.domain.orders.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.orders.dto.OrderRequest;
import spharos.msg.domain.orders.dto.OrderRequest.OrderDto;
import spharos.msg.domain.orders.dto.OrderResponse;
import spharos.msg.domain.orders.dto.OrderResponse.PriceInfo;
import spharos.msg.domain.orders.entity.Orders;
import spharos.msg.domain.orders.repository.OrderRepository;
import spharos.msg.domain.product.entity.Product;
import spharos.msg.domain.product.repository.ProductOptionRepository;
import spharos.msg.domain.users.entity.Address;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.AddressRepository;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.code.status.ErrorStatus;
import spharos.msg.global.api.exception.OrderException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final AddressRepository addressRepository;
    private final ProductOptionRepository productOptionRepository;

    public OrderResponse.OrderUserDto findOrderUser(String uuid) {
        Users user = tryGetUser(uuid);
        String address = tryGetAddress(user).getAddress();

        return OrderResponse.OrderUserDto
            .builder()
            .loginId(user.getLoginId())
            .email(user.getEmail())
            .username(user.getUsername())
            .phoneNumber(user.getPhoneNumber())
            .address(address)
            .build();
    }

    @Transactional
    public OrderResponse.OrderProductDto saveOrder(List<OrderRequest.OrderDto> orderDtos,
        String uuid) {
        Users user = tryGetUser(uuid);
        Address addressInfo = tryGetAddress(user);
        Orders newOrder = generateOrder(orderDtos, user, addressInfo);
        Long orderId = orderRepository.save(newOrder).getId();
        return OrderResponse.OrderProductDto
            .builder()
            .loginId(user.getLoginId())
            .address(addressInfo.getAddress())
            .username(user.getUsername())
            .phoneNumber(user.getPhoneNumber())
            .orderId(orderId)
            .priceInfos(createPriceInfos(orderDtos))
            .totalPrice(calculateTotalPrice(orderDtos))
            .build();
    }

    private Users tryGetUser(String uuid) {
        return usersRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> new OrderException(ErrorStatus.INVALID_ORDER));
    }

    private Address tryGetAddress(Users user) {
        return addressRepository
                .findById(user.getId())
            .orElseThrow(
                () -> new OrderException(ErrorStatus.ORDER_ADDRESS_NOT_FOUND));
    }

    private Orders generateOrder(List<OrderDto> orderDtos, Users user, Address addressInfo) {
        return Orders
            .builder()
            .buyerId(user.getId())
            .buyerPhoneNumber(user.getPhoneNumber())
            .buyerName(user.getUsername())
            .address(addressInfo.getAddress())
            .totalAmount(sumOrderAmount(orderDtos))
            .build();
    }

    private Long sumOrderAmount(List<OrderDto> orderDtos) {
        return orderDtos
            .stream()
            .mapToLong(OrderDto::getQuantity)
            .sum();
    }

    private List<PriceInfo> createPriceInfos(List<OrderDto> orderDtos) {
        return orderDtos
            .stream()
            .map(this::createPriceInfo)
            .toList();
    }

    private PriceInfo createPriceInfo(OrderDto o) {
        Product product = findProductByOption(o.getProductOptionId());
        return new PriceInfo(
            product.getMinDeliveryFee(),
            product.getProductPrice(),
            getDiscountPrice(product));
    }

    private Long getDiscountPrice(Product product) {
        return (product.getProductPrice() *
            (100 - product.getDiscountRate().longValue())) / 100;
    }

    private Long calculateEachProductPrice(OrderDto orderDto) {
        Product orderProduct = findProductByOption(orderDto.getProductOptionId());
        return (long) orderProduct.getProductPrice() * orderDto.getQuantity();
    }

    private Product findProductByOption(Long optionId) {
        return productOptionRepository
            .findById(optionId)
            .orElseThrow(() -> new OrderException(ErrorStatus.ORDER_PRODUCT_NOT_FOUND))
            .getProduct();
    }

    private Long calculateTotalPrice(List<OrderDto> orderDtos) {
        return orderDtos.stream()
            .mapToLong(this::calculateEachProductPrice)
            .sum();
    }
}
