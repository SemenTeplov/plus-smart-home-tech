package ru.yandex.practicum.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.persistence.entity.Product;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto product);

    void updateProduct(@MappingTarget Product product, ProductDto productDto);
}
