package spharos.msg.domain.review.service;

import static spharos.msg.global.api.code.status.ErrorStatus.REVIEW_DELETE_FAIL;
import static spharos.msg.global.api.code.status.ErrorStatus.REVIEW_READ_FAIL;
import static spharos.msg.global.api.code.status.ErrorStatus.REVIEW_SAVE_FAIL;
import static spharos.msg.global.api.code.status.ErrorStatus.REVIEW_UPDATE_FAIL;
import static spharos.msg.global.api.code.status.SuccessStatus.REVIEW_DELETE_SUCCESS;
import static spharos.msg.global.api.code.status.SuccessStatus.REVIEW_READ_SUCCESS;
import static spharos.msg.global.api.code.status.SuccessStatus.REVIEW_SAVE_SUCCESS;
import static spharos.msg.global.api.code.status.SuccessStatus.REVIEW_UPDATE_SUCCESS;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.product.entity.Product;
import spharos.msg.domain.product.repository.ProductRepository;
import spharos.msg.domain.review.dto.ReviewRequest;
import spharos.msg.domain.review.dto.ReviewResponse;
import spharos.msg.domain.review.entity.Review;
import spharos.msg.domain.review.repository.ReviewRepository;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.ApiResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public ApiResponse<?> getReviewDetail(Long reviewId) {
        try {
            //리뷰 객체 가져 오기
            Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
            if (reviewOptional.isPresent()) {
                Review review = reviewOptional.get();
                Optional<Users> usersOptional = usersRepository.findById(review.getUserId());
                String userName;

                if (usersOptional.isPresent()) {
                    Users users = usersOptional.get();
                    userName = users.getUsername(); //추후 사용자 이름이 들어가도록 수정 필요
                } else {
                    userName = "탈퇴한 회원입니다";
                }

                return ApiResponse.of(REVIEW_READ_SUCCESS,ReviewResponse.ReviewDetail.builder()
                    .reviewId(review.getId())
                    .reviewStar(review.getReviewStar())
                    .reviewCreatedat(review.getCreatedAt())
                    .reviewContent(review.getReviewComment())
                    .reviewer(userName)
                    .build());
            }
            return ApiResponse.onFailure(REVIEW_READ_FAIL,null);
        } catch (Exception e) {
            return ApiResponse.onFailure(REVIEW_READ_FAIL,null);
        }
    }

    @Transactional
    public ApiResponse<?> saveReview(Long productId, ReviewRequest.createDto reviewRequest,
        String userUuid) {
        try {//상품 객체 가져오기
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                //유저 가져오기
                Users users = usersRepository.findByUuid(userUuid).orElseThrow();
                Long userId = users.getId();
                //추후, 이미 작성된 리뷰 인지 확인 필요함
                //리뷰 객체 생성
                Review review = Review.builder()
                    .product(product)
                    .reviewStar(reviewRequest.getReviewStar())
                    .reviewComment(reviewRequest.getReviewContent())
                    .userId(userId)
                    .build();
                //저장
                reviewRepository.save(review);
                return ApiResponse.of(REVIEW_SAVE_SUCCESS, null);
            }
            return ApiResponse.onFailure(REVIEW_SAVE_FAIL, null);
        } catch (Exception e) {
            log.info("에러 발생 " + e.getMessage());
            return ApiResponse.onFailure(REVIEW_SAVE_FAIL, null);
        }
    }

    @Transactional
    public ApiResponse<?> updateReview(Long reviewId, ReviewRequest.updateDto reviewRequest) {
        try {
            //id로 기존 리뷰 찾기
            Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
            if (reviewOptional.isPresent()) {
                Review existingReview = reviewOptional.get();

                //리뷰 수정
                existingReview.updateReview(reviewRequest.getReviewContent(),
                    reviewRequest.getReviewStar());

                return ApiResponse.of(REVIEW_UPDATE_SUCCESS, null);
            }
            return ApiResponse.onFailure(REVIEW_UPDATE_FAIL, null);
        } catch (Exception e) {
            log.info("에러 발생 " + e.getMessage());
            return ApiResponse.onFailure(REVIEW_UPDATE_FAIL, null);
        }
    }

    @Transactional
    public ApiResponse<?> deleteReview(Long reviewId) {
        try {
            //id와 일치 하는 리뷰 삭제
            reviewRepository.deleteById(reviewId);
            return ApiResponse.of(REVIEW_DELETE_SUCCESS, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(REVIEW_DELETE_FAIL, null);
        }
    }
}
