package spharos.msg.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spharos.msg.domain.review.dto.ReviewRequest;
import spharos.msg.domain.review.service.ReviewService;
import spharos.msg.global.api.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-review")
@Slf4j
@Tag(name="Review",description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "상품 리뷰 상세 조회",
    description = "id로 상품 리뷰를 조회합니다")
    @GetMapping("/{reviewId}")
    public ApiResponse<?> getReviewDetail(
        @PathVariable("reviewId") Long reviewId
    ){
     return   reviewService.getReviewDetail(reviewId);
    }
    @Operation(summary = "상품 리뷰 등록",
        description = "리뷰가 가능한 주문상세에 대해 상품 리뷰를 등록합니다")
    @PostMapping("/{productId}")
    public ApiResponse<?> saveReview(
        @PathVariable("productId") Long productId,
        @RequestBody ReviewRequest.createDto reviewRequest,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
    return reviewService.saveReview(productId, reviewRequest,userDetails.getUsername());
    }

    @Operation(summary = "상품 리뷰 수정",
    description = "리뷰 id와 일치하는 리뷰의 별점과 내용을 수정합니다")
    @PatchMapping("/{reviewId}")
    public ApiResponse<?> updateReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestBody ReviewRequest.updateDto reviewRequest
    ){
        return reviewService.updateReview(reviewId, reviewRequest);
    }

    @Operation(summary = "상품 리뷰 삭제",
        description = "리뷰 id와 일치하는 리뷰를 삭제합니다")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<?> deleteReview(
        @PathVariable("reviewId") Long reviewId
    ){
        return reviewService.deleteReview(reviewId);
    }
}
