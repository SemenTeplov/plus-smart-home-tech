package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.persistence.enums.ProductCategory;
import ru.yandex.practicum.persistence.enums.ProductState;
import ru.yandex.practicum.persistence.enums.QuantityState;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    String productId;

    String productName;

    String description;

    String imageSrc;

    QuantityState quantityState;

    ProductState productState;

    ProductCategory productCategory;

    Double price;

    @Override
    public String toString() {
        return String.format(
                "id: %s, name: %s, description: %s, image: %s, quantity state: %s, product state: %s, category: %s, price: %.2f",
                productId, productName, description, imageSrc, quantityState.name(), productState.name(),
                productCategory.name(), price);
    }
}
