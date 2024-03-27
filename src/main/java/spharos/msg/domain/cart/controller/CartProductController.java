package spharos.msg.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import spharos.msg.domain.cart.dto.CartProductQuantityDto;
import spharos.msg.domain.cart.service.CartProductService;
import spharos.msg.domain.cart.service.CartProductUpdateService;
import spharos.msg.global.api.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Tag(name = "CartProduct", description = "장바구니 API")
public class CartProductController {
    private final CartProductService cartProductService;
    private final CartProductUpdateService cartProductUpdateService;

    @Operation(summary = "장바구니 담기",
            description = "옵션에 해당되는 상품을 장바구니에 추가합니다.")
    @PostMapping("/option/{productOptionId}")
    public ApiResponse<?> addCart(
            @PathVariable Long productOptionId,
            @RequestBody CartProductQuantityDto cartProductQuantity,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return cartProductService.addCartProduct(productOptionId, cartProductQuantity, userDetails.getUsername());
    }

    @Operation(summary = "장바구니 조회",
            description = "장바구니에 담긴 상품들을 조회합니다.")
    @GetMapping
    public ApiResponse<?> getCart(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return cartProductService.getCart(userDetails.getUsername());
    }

    @Operation(summary = "장바구니 옵션 수정",
            description = "장바구니에 담긴 상품의 옵션을 수정합니다.")
    @PatchMapping("/option/{cartId}")
    public ApiResponse<?> updateCartProductOption(
            @PathVariable Long cartId,
            @RequestParam("productOptionId") Long productOptionId
    ) {
        return cartProductUpdateService.updateCartProductOption(productOptionId, cartId);
    }

    @Operation(summary = "장바구니 수량 추가",
            description = "장바구니에 담긴 상품의 수량을 늘립니다.")
    @PatchMapping("/add/{cartId}")
    public ApiResponse<?> addCartProductQuantity(
            @PathVariable Long cartId
    ) {
        return cartProductUpdateService.addCartProductQuantity(cartId);
    }

    @Operation(summary = "장바구니 수량 감소",
            description = "장바구니에 담긴 상품의 수량을 줄입니다.")
    @PatchMapping("/minus/{cartId}")
    public ApiResponse<?> minusCartProductQuantity(
            @PathVariable Long cartId
    ) {
        return cartProductUpdateService.minusCartProductQuantity(cartId);
    }

    @Operation(summary = "장바구니 체크 수정",
            description = "장바구니에 담긴 상품을 체크합니다.")
    @PatchMapping("/check/{cartId}")
    public ApiResponse<?> checkCartProduct(
            @PathVariable Long cartId
    ) {
        return cartProductUpdateService.checkCartProduct(cartId);
    }

    @Operation(summary = "장바구니 체크 수정",
            description = "장바구니에 담긴 상품을 체크해제합니다.")
    @PatchMapping("/not-check/{cartId}")
    public ApiResponse<?> notCheckCartProduct(
            @PathVariable Long cartId
    ) {
        return cartProductUpdateService.notCheckCartProduct(cartId);
    }


    @Operation(summary = "장바구니 삭제",
            description = "장바구니에 담긴 상품을 삭제합니다.")
    @DeleteMapping("/{cartId}")
    public ApiResponse<?> deleteCart(
            @PathVariable Long cartId
    ) {
        return cartProductService.deleteCart(cartId);
    }

    @Operation(summary = "장바구니 상품 옵션 조회",
            description = "장바구니에 담긴 상품의 옵션을 조회합니다.")
    @GetMapping("/option/{productId}")
    public ApiResponse<?> getCartOption(
            @PathVariable Long productId
    ) {
        return cartProductService.getCartOption(productId);
    }
}