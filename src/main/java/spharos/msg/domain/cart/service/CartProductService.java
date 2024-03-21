package spharos.msg.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.cart.dto.CartProductOptionResponseDto;
import spharos.msg.domain.cart.dto.CartProductRequestDto;
import spharos.msg.domain.cart.dto.CartProductResponseDto;
import spharos.msg.domain.cart.entity.CartProduct;
import spharos.msg.domain.cart.repository.CartProductRepository;
import spharos.msg.domain.product.entity.Product;
import spharos.msg.domain.product.entity.ProductOption;
import spharos.msg.domain.product.repository.ProductOptionRepository;
import spharos.msg.domain.product.repository.ProductRepository;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.ApiResponse;
import spharos.msg.global.api.code.status.SuccessStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UsersRepository usersRepository;

    //todo 회원/비회원 구분 및 예외처리
    @Transactional
    public ApiResponse<?> addCart(Long productOptionId, CartProductRequestDto cartProductRequestDto, String userUuid) {
        ProductOption productOption = productOptionRepository.findById(productOptionId).orElseThrow();
        Users users = usersRepository.findByUuid(userUuid).orElseThrow();

        //옵션 없는 상품
        if(productOption.getOptionColor()==null&&productOption.getOptionSize()==null&&productOption.getOptionEtc()==null){
            List<CartProduct> cartProducts = cartProductRepository.findByUsers(users);
            for(CartProduct cartProduct:cartProducts){
                if(cartProduct.getProductOption().getProductOptionId().equals(productOptionId)){
                    cartProduct.addCartProductQuantity(1);
                    return ApiResponse.of(SuccessStatus.CART_PRODUCT_ADD_SUCCESS, null);
                }
            }
            CartProduct cartProduct = CartProduct.builder()
                    .cartProductQuantity(cartProductRequestDto.getProductQuantity())
                    .cartIsChecked(false)
                    .users(users)
                    .productOption(productOption)
                    .build();
            cartProductRepository.save(cartProduct);
            return ApiResponse.of(SuccessStatus.CART_PRODUCT_ADD_SUCCESS, null);
        }
        //옵션 있는 상품
        List<CartProduct> cartProducts = cartProductRepository.findByUsers(users);
        for(CartProduct cartProduct:cartProducts){
            if(cartProduct.getProductOption().getProductOptionId().equals(productOptionId)){
                cartProduct.addCartProductQuantity(cartProductRequestDto.getProductQuantity());
                return ApiResponse.of(SuccessStatus.CART_PRODUCT_ADD_SUCCESS, null);
            }
        }
        CartProduct cartProduct = CartProduct.builder()
                .cartProductQuantity(cartProductRequestDto.getProductQuantity())
                .cartIsChecked(false)
                .users(users)
                .productOption(productOption)
                .build();
        cartProductRepository.save(cartProduct);
        return ApiResponse.of(SuccessStatus.CART_PRODUCT_ADD_SUCCESS, null);
    }

    @Transactional
    public ApiResponse<?> getCart(String userUuid) {
        Users users = usersRepository.findByUuid(userUuid).orElseThrow();

        return ApiResponse.of(SuccessStatus.CART_PRODUCT_GET_SUCCESS,
                cartProductRepository.findByUsers(users)
                        .stream()
                        .map(CartProductResponseDto::new)
                        .collect(Collectors.toList()));
    }

    @Transactional
    public ApiResponse<?> updateCart(CartProductRequestDto cartProductRequestDto, Long cartId, String userUuid) {
        CartProduct cartProduct = cartProductRepository.findById(cartId).orElseThrow();
        ProductOption productOption = productOptionRepository.findById(cartProductRequestDto.getProductOptionId()).orElseThrow();
        Users users = usersRepository.findByUuid(userUuid).orElseThrow();

        cartProduct.updateCartProduct(cartProductRequestDto, productOption);

        return ApiResponse.of(SuccessStatus.CART_PRODUCT_UPDATE_SUCCESS,
                new CartProductResponseDto(cartProduct));
    }

    @Transactional
    public ApiResponse<?> deleteCart(Long cartId, String userUuid) {
        CartProduct cartProduct = cartProductRepository.findById(cartId).orElseThrow();
        Users users = usersRepository.findByUuid(userUuid).orElseThrow();
        cartProductRepository.delete(cartProduct);

        return ApiResponse.of(SuccessStatus.CART_PRODUCT_DELETE_SUCCESS, null);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getCartOption(Long productId, String userUuid) {
        Product product = productRepository.findById(productId).orElseThrow();

        return ApiResponse.of(SuccessStatus.CART_PRODUCT_OPTION_SUCCESS,
                productOptionRepository.findByProduct(product)
                        .stream()
                        .map(CartProductOptionResponseDto::new)
                        .collect(Collectors.toList()));
    }
}