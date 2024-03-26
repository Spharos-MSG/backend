package spharos.msg.domain.review.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

public class ReviewResponse {
    @Data
    @Builder
    @Setter(AccessLevel.NONE)
    public static class ReviewResponseDto {
        private List<ReviewDetail> productReviews;
    }
    @Data
    @Builder
    @Setter(AccessLevel.NONE)
    public static class ReviewDetail {
        private Long reviewId;
        private BigDecimal reviewStar;
        private LocalDateTime reviewCreatedat;
        private String reviewContent;
        private String reviewer;
    }
}
