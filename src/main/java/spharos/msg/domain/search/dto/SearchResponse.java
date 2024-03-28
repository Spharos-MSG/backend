package spharos.msg.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchResponse {

    @NoArgsConstructor
    @Getter
    @ToString
    @AllArgsConstructor
    public static class SearchProductDtos {

        @JsonProperty("isLast")
        Boolean isLast = false;
        List<SearchProductDto> searchProductDtos;
    }

    @NoArgsConstructor
    @Getter
    @ToString
    @AllArgsConstructor
    public static class SearchProductDto {

        Long productId;
    }

    @NoArgsConstructor
    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class SearchInputDto {

        String productName;
    }

}
