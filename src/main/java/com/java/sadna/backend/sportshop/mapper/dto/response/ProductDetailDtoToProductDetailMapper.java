package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductDetail;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductSize;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductDetailDtoToProductDetailMapper implements BaseMapper<ProductDetailDto, ProductDetail> {

    private final ImagesProperties imagesProperties;
    private final ProductSizeDtoToProductSizeMapper productSizeDtoToProductSizeMapper;

    public ProductDetailDtoToProductDetailMapper(ImagesProperties imagesProperties,
                                                 ProductSizeDtoToProductSizeMapper productSizeDtoToProductSizeMapper) {
        this.imagesProperties = imagesProperties;
        this.productSizeDtoToProductSizeMapper = productSizeDtoToProductSizeMapper;
    }

    @Override
    public ProductDetail map(ProductDetailDto source) {
        ProductDto product = source.getProduct();
        List<ProductSize> sizes = source.getSizes().stream()
                .map(productSizeDtoToProductSizeMapper::map)
                .toList();
        return new ProductDetail()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .isMultiSize(product.isMultiSize())
                .imageUrl(imagesProperties.getProductImageUrl(product.getImageFilename()))
                .price(product.getPrice())
                .version(product.getVersion())
                .isArchived(product.isArchived())
                .updatedAt(product.getUpdatedAt())
                .updatedBy(product.getUpdatedBy())
                .archivedAt(product.getArchivedAt())
                .archivedBy(product.getArchivedBy())
                .categoryName(source.getCategoryName())
                .sizes(sizes);
    }
}
