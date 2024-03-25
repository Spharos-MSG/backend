package spharos.msg.domain.likes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import spharos.msg.domain.likes.entity.Likes;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class LikesResponseDto {
    private Long productId;
    private String productName;
    private int productPrice;
//    private BigDecimal productStar;
//    private Long commentCount;
    private BigDecimal discountRate;
    private int discountedPrice;

    public LikesResponseDto(Likes likes) {
        this.productId = likes.getProduct().getId();
        this.productName = likes.getProduct().getProductName();
        this.productPrice = likes.getProduct().getProductPrice();
//        this.productStar = likes.getProduct().getProductSalesInfo().getProductStars();
//        this.commentCount = likes.getProduct().getProductSalesInfo().getReviewCount();
        this.discountRate = likes.getProduct().getDiscountRate();
        this.discountedPrice = likes.getProduct().getProductPrice()*(100-Integer.parseInt(String.valueOf(likes.getProduct().getDiscountRate())))/100;
    }
}
