package spharos.msg.domain.product.service;

import jakarta.transaction.Transactional;
import java.math.RoundingMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import spharos.msg.domain.category.entity.CategoryProduct;
import spharos.msg.domain.category.repository.CategoryProductRepository;

import spharos.msg.domain.product.dto.ProductResponse;
import spharos.msg.domain.product.entity.Product;
import spharos.msg.domain.product.entity.ProductImage;

import spharos.msg.domain.product.repository.ProductImageRepository;
import spharos.msg.domain.product.repository.ProductRepository;
import spharos.msg.domain.product.repository.ProductRepositoryCustom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryProductRepository categoryProductRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductRepositoryCustom productRepositoryCustom;

    //id로 상품의 기본 정보 불러오기
    @Transactional
    public ProductResponse.ProductInfoDto getProductInfo(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(productId + "해당 상품을 찾을 수 없음"));

        Integer discountPrice = getDiscountedPrice(product.getProductPrice(),
            product.getDiscountRate());

        return ProductResponse.ProductInfoDto.builder()
            .productBrand(product.getBrand().getBrandName())
            .productName(product.getProductName())
            .productPrice(product.getProductPrice())
            .productStar(product.getProductSalesInfo().getProductStar())
            .discountRate(product.getDiscountRate())
            .discountPrice(discountPrice)
            .build();
    }

    //id로 상품 썸네일 이미지 불러오기
    @Transactional
    public ProductResponse.ProductImage getProductImage(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(productId + "해당 상품을 찾을 수 없음"));

        ProductImage productImage = productImageRepository.findByProductAndImageIndex(product, 0)
            .orElseThrow(() -> new NotFoundException("해당 상품에 대한 index가 0인 이미지를 찾을 수 없음"));

        return ProductResponse.ProductImage.builder()
            .productImageUrl(productImage.getProductImageUrl())
            .productImageDescription(productImage.getProductImageDescription())
            .build();
    }

    //id로 상품 이미지들 불러오기
    @Transactional
    public List<ProductResponse.ProductImage> getProductImages(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(productId + "해당 상품을 찾을 수 없음"));

        List<ProductImage> productImages = productImageRepository.findByProduct(product);

        return productImages.stream().map(productImage -> ProductResponse.ProductImage.builder()
            .productImageUrl(productImage.getProductImageUrl())
            .productImageDescription(productImage.getProductImageDescription())
            .build()).toList();
    }

    //id로 상품 상세 html 불러오기
    @Transactional
    public String getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(productId + "해당 상품을 찾을 수 없음"));

        return product.getProductInfoDetail().getProductInfoDetailContent();
    }

    //id로 상품 상세 카테고리 정보 불러오기
    @Transactional
    public ProductResponse.ProductCategory getProductCategory(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(productId + "해당 상품을 찾을 수 없음"));
        CategoryProduct categoryProduct = categoryProductRepository.findByProduct(product);

        return ProductResponse.ProductCategory.builder()
            .categoryMid(categoryProduct.getCategory().getCategoryName())
            .categoryLarge(categoryProduct.getCategory().getParent().getCategoryName())
            .build();
    }

    //id리스트로 상품 객체 불러오기
    @Transactional
    public List<ProductResponse.ProductInfoDto> getProductsDetails(List<Long> idList) {
        List<Product> products = productRepositoryCustom.findProductsByIdList(idList);
        return products.stream().map(product -> {
            ProductImage productImage = productImageRepository.findByProductAndImageIndex(product,
                    0)
                .orElseGet(ProductImage::new); // 이미지를 찾을 수 없을 때 빈 ProductImage 객체를 생성하여 반환

            return ProductResponse.ProductInfoDto.builder()
                .productName(product.getProductName())
                .productBrand(product.getBrand().getBrandName())
                .productImage(productImage.getProductImageUrl())
                .productPrice(product.getProductPrice())
                .discountRate(product.getDiscountRate())
                .discountPrice(
                    getDiscountedPrice(product.getProductPrice(), product.getDiscountRate()))
                .productStar(product.getProductSalesInfo().getProductStar())
                .reviewCount(product.getProductSalesInfo().getReviewCount())
                .build();
        }).toList();
    }

    //베스트 상품 목록 가져오기
    @Transactional
    public ProductResponse.BestProductsDto getRankingProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByOrderByProductSalesInfoProductSellTotalCountDesc(
            pageable);

        boolean isLast = !productPage.hasNext();

        return ProductResponse.BestProductsDto.builder()
            .productList(productPage.getContent().stream().map(
                    product -> ProductResponse.ProductIdDto.builder().productId(product.getId())
                        .build())
                .toList()).isLast(isLast).build();
    }

    //어드민 베스트11 불러 오기
    public List<ProductResponse.Best11Dto> getBest11Products() {
        List<Product> products = productRepository.findAllByOrderByProductSalesInfoProductSellTotalCountDesc();

        return products.stream().limit(11)
            .map(product -> {
                ProductImage productImage = productImageRepository.findByProductAndImageIndex(product, 0)
                    .orElse(new ProductImage());

                return ProductResponse.Best11Dto.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .productBrand(product.getBrand().getBrandName())
                    .productImage(productImage.getProductImageUrl())
                    .productSellTotalCount(product.getProductSalesInfo().getProductSellTotalCount())
                    .build();
            })
            .toList();
    }

    //할인가 계산하는 method
    private Integer getDiscountedPrice(Integer price, BigDecimal discountRate) {

        if (price == null || discountRate == null) {
            return price;
        }

        BigDecimal normalPrice = new BigDecimal(price);
        BigDecimal discount = discountRate.divide(BigDecimal.valueOf(100),
            RoundingMode.HALF_UP); //할인율을 백분율로 변환
        BigDecimal discountedPrice = normalPrice.multiply(
            BigDecimal.ONE.subtract(discount)); // 할인 적용 가격 계산

        return discountedPrice.intValue();
    }
}
