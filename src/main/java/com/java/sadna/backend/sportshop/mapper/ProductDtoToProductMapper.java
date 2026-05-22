package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.products.model.Product;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoToProductMapper implements BaseMapper<ProductDto, Product> {

    private final ImagesProperties imagesProperties;

    public ProductDtoToProductMapper(AppProperties appProperties) {
        this.imagesProperties = appProperties.getImages();
    }

    @Override
    public Product map(ProductDto product) {
        Product apiProduct = new Product(
                product.getId(),
                product.getName(),
                product.getCategoryId(),
                product.isMultiSize(),
                product.getPrice(),
                product.getVersion()
        );
        apiProduct.setDescription(product.getDescription());
        apiProduct.setImageUrl(imagesProperties.getProductImageUrl(product.getImageFilename()));
        return apiProduct;
    }
}
