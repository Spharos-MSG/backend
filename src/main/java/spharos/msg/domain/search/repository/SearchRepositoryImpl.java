package spharos.msg.domain.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spharos.msg.domain.category.entity.QCategory;
import spharos.msg.domain.category.entity.QCategoryProduct;
import spharos.msg.domain.product.entity.QProduct;
import spharos.msg.domain.search.dto.SearchResponse.SearchInputDto;
import spharos.msg.domain.search.dto.SearchResponse.SearchProductDto;
import spharos.msg.domain.search.dto.SearchResponse.SearchProductDtos;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SearchRepositoryImpl implements SearchRepository {

    private static final int SEARCH_PRODUCT_SIZE = 10;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SearchProductDtos searchAllProduct(String keyword, int index) {
        QCategoryProduct categoryProduct = QCategoryProduct.categoryProduct;
        QProduct product = QProduct.product;
        QCategory category = QCategory.category;

        Long totalSize = getTotalSize(keyword, categoryProduct, product, category);
        List<SearchProductDto> searchProductDtos = getSearchProductIds(
            keyword, index, categoryProduct, product, category);

        boolean isLast = (index + 1) * SEARCH_PRODUCT_SIZE >= totalSize;

        return new SearchProductDtos(isLast, searchProductDtos);
    }

    private Long getTotalSize(String keyword, QCategoryProduct categoryProduct, QProduct product,
        QCategory category) {
        return jpaQueryFactory
            .select(categoryProduct.countDistinct())
            .from(categoryProduct)
            .innerJoin(categoryProduct.product, product)
            .innerJoin(categoryProduct.category, category)
            .where(isContainsNameOrBrandOrCategory(keyword, product, category))
            .fetchOne();
    }

    private List<SearchProductDto> getSearchProductIds(String keyword, long index,
        QCategoryProduct categoryProduct, QProduct product, QCategory category) {
        List<SearchProductDto> searchProductDtos = jpaQueryFactory
            .select(Projections.constructor(
                SearchProductDto.class, product.id))
            .from(categoryProduct)
            .innerJoin(categoryProduct.product, product)
            .innerJoin(categoryProduct.category, category)
            .where(isContainsNameOrBrandOrCategory(keyword, product, category))
            .distinct()
            .offset(index * SEARCH_PRODUCT_SIZE)
            .limit(SEARCH_PRODUCT_SIZE)
            .fetch();

        searchProductDtos.sort((p1, p2) ->
            p1.getProductId().compareTo(p2.getProductId()));
        return searchProductDtos;
    }

    private BooleanExpression isContainsNameOrBrandOrCategory(String keyword, QProduct product,
        QCategory category) {
        return product.productName.contains(keyword)
            .or(product.productBrand.contains(keyword))
            .or(category.categoryName.contains(keyword));
    }

    @Override
    public List<SearchInputDto> searchAllKeyword(String keyword) {
        QCategoryProduct categoryProduct = QCategoryProduct.categoryProduct;
        QProduct product = QProduct.product;
        QCategory category = QCategory.category;
        return jpaQueryFactory
            .select(Projections.constructor(SearchInputDto.class,
                product.productName))
            .from(categoryProduct)
            .innerJoin(categoryProduct.product, product)
            .innerJoin(categoryProduct.category, category)
            .where(
                product.productName.eq(keyword)
                    .or(product.productName.contains(keyword))
                    .or(category.categoryName.contains(keyword))
            )
            .distinct()
            .limit(SEARCH_PRODUCT_SIZE)
            .fetch();
    }

}
