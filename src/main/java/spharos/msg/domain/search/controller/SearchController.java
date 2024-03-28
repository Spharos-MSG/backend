package spharos.msg.domain.search.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spharos.msg.domain.search.dto.SearchResponse.SearchInputDto;
import spharos.msg.domain.search.dto.SearchResponse.SearchProductDtos;
import spharos.msg.domain.search.service.SearchService;
import spharos.msg.global.api.ApiResponse;
import spharos.msg.global.api.code.status.SuccessStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/")
@Tag(name = "Search", description = "검색 API")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @GetMapping("search")
    public ApiResponse<SearchProductDtos> searchResultAPI(
        @RequestParam(value = "keyword") String keyword,
        @RequestParam(value = "index", required = false, defaultValue = "0") int index) {
        SearchProductDtos searchProductDtos = searchService.findMatchProducts(keyword, index);
        return ApiResponse.of(SuccessStatus.SEARCH_RESULT_SUCCESS, searchProductDtos);
    }

    @GetMapping("search-list")
    public ApiResponse<List<SearchInputDto>> searchInputDto(
        @RequestParam(value = "keyword") String keyword
    ) {
        List<SearchInputDto> searchInputDtos = searchService.findExpectedKeywords(keyword);
        return ApiResponse.of(SuccessStatus.SEARCH_INPUT_SUCCESS, searchInputDtos);
    }
}
