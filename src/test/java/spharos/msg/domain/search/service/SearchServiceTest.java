package spharos.msg.domain.search.service;

import static java.util.Comparator.comparingInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.product.repository.ProductRepository;
import spharos.msg.domain.search.dto.SearchResponse.SearchInputDto;
import spharos.msg.domain.search.dto.SearchResponse.SearchProductDtos;

@SpringBootTest
@Transactional
@Slf4j
class SearchServiceTest {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ProductRepository productRepository;

    private List<String> getProductNamesByDto(SearchProductDtos result) {
        return result.getSearchProductDtos()
            .stream()
            .map(e -> productRepository.findById(e.getProductId())
                .get()
                .getProductName())
            .toList();
    }

    private List<String> getSearchResult(String keyword) {
        List<SearchInputDto> expectedKeywords = searchService.findExpectedKeywords(keyword);
        return expectedKeywords
            .stream()
            .map(SearchInputDto::getProductName)
            .toList();
    }

    @MethodSource
    private static Stream<Arguments> createParam() {
        return Stream.of(
            arguments("[!]클래식"),
            arguments("탑텐!@ "),
            arguments("리바이스 여성 "),
            arguments(" 리바이스     여성")
        );
    }

    @ParameterizedTest
    @MethodSource("createParam")
    @DisplayName("검색 키워드에 공백,한글,영어,숫자를 제외한 그 어떤 것도 검출되면 안된다.")
    void 키워드_필터링_성공_테스트(String keyword) {
        List<String> keywords = getSearchResult(keyword);
        Pattern pattern = Pattern.compile("[^\\s\\w가-힣]");

        assertThat(keywords)
            .isNotEmpty()
            .noneMatch(e -> pattern.matcher(e).find());
    }

    @ParameterizedTest
    @MethodSource("createParam")
    @DisplayName("검색된 키워드들은 길이 순으로 오름차순으로 정렬되어야 한다.")
    void 키워드_정렬_성공_테스트(String keyword) {
        //given
        List<String> keywords = getSearchResult(keyword);

        assertThat(keywords)
            .isSortedAccordingTo(comparingInt(String::length));
    }

    @ParameterizedTest
    @MethodSource("createParam")
    @DisplayName("검색된 키워드에 중복된 값이 존재하면 안된다.")
    void 키워드_중복_테스트_성공(String keyword) {
        //given
        List<String> keywords = getSearchResult(keyword);
        assertThat(new HashSet<>(keywords).size())
            .isEqualTo(keywords.size());
    }

    @Test
    @DisplayName("특정 브랜드 이름이 포함되면 그 브랜드의 Product를 반환한다.")
    void 상품_브랜드_검색_성공_테스트() {
        //given
        SearchProductDtos result = searchService.findMatchProducts("탑텐", 0);
        //when, then
        List<String> productNames = getProductNamesByDto(result);
        assertThat(productNames)
            .isNotEmpty()
            .allMatch(e -> e.contains("탑텐"));
    }

    @ParameterizedTest
    @MethodSource("createParam")
    @DisplayName("특정 이름이 포함되면 그 이름이 포함된 Product를 반환한다.")
    void 상품_이름_검색_성공_테스트(String keyword) {
        //given
        String containsWord = keyword
            .replaceAll("\\s{2,}", " ")
            .replaceAll("[^a-zA-Z가-힣0-9\\s]", "")
            .trim();

        SearchProductDtos result = searchService.findMatchProducts(keyword, 0);
        //when, then

        List<String> productNames = getProductNamesByDto(result);
        assertThat(productNames)
            .isNotEmpty()
            .allMatch(e -> e.contains(containsWord));
    }

    @Test
    @DisplayName("검색 상품이 10개가 넘을시, 10개까지만 반환하도록 한다.")
    void 상품_개수_성공_테스트() {
        //given
        SearchProductDtos result = searchService.findMatchProducts("여성", 0);
        //when, then
        assertThat(result.getSearchProductDtos()).hasSize(10);
    }

    @Test
    @DisplayName("현재 인덱스와 다음 인덱스의 제품이 중복되면 안된다")
    void 인덱스_조회_성공_테스트() {
        SearchProductDtos result1 = searchService.findMatchProducts("여성", 0);
        SearchProductDtos result2 = searchService.findMatchProducts("여성", 1);

        assertThat(result1.getSearchProductDtos()).noneMatch(
            result2.getSearchProductDtos()::contains);
    }

    @Test
    @DisplayName("존재하지 않는 상품이면, 빈 리스트를 반환한다.")
    void 빈_리스트_반환() {
        SearchProductDtos result = searchService.findMatchProducts("존재하지 않는 상품 ", 0);
        assertThat(result.getSearchProductDtos()).isEmpty();
    }

    @Test
    @DisplayName("카테고리로 검색할 시, 결과가 나와야한다.")
    void 카테고리_검색() {
        SearchProductDtos result = searchService.findMatchProducts("SEASONAL", 0);
        assertThat(result.getSearchProductDtos()).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("배열 개수가 있다면 isLast가 true, 없다면 false")
    void isLast_테스트(int index) {
        SearchProductDtos result = searchService.findMatchProducts("여성", index);
        boolean expected = result.getSearchProductDtos().isEmpty();
        assertThat(result.getIsLast()).isEqualTo(expected);
    }
}