package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import ru.yandex.practicum.status.ProductCategory;
import ru.yandex.practicum.status.ProductState;
import ru.yandex.practicum.status.QuantityState;

import java.math.BigDecimal;

@Getter
@Setter
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

    BigDecimal price;

    @Override
    public String toString() {
        return String.format(
                "id: %s, name: %s, description: %s, image: %s, quantity state: %s, product state: %s, category: %s, price: %.2f",
                productId, productName, description, imageSrc, quantityState.name(), productState.name(),
                productCategory.name(), price);
    }
}
