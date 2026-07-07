package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.products.model.Product;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoToProductMapper implements BaseMapper<ProductDto, Product> {

    private final ImagesProperties imagesProperties;

    public ProductDtoToProductMapper(ImagesProperties imagesProperties) {
        this.imagesProperties = imagesProperties;
    }

    @Override
    public Product map(ProductDto product) {
        return new Product()
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
                .archivedBy(product.getArchivedBy());
    }
}
