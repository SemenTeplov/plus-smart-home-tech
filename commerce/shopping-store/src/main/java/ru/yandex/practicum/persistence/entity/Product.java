package ru.yandex.practicum.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import ru.yandex.practicum.persistence.enums.ProductCategory;
import ru.yandex.practicum.persistence.enums.ProductState;
import ru.yandex.practicum.persistence.enums.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID productId;

    String productName;

    String description;

    String imageSrc;

    @Enumerated(EnumType.STRING)
    QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    ProductState productState;

    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    Double price;

    @Override
    public String toString() {
        return String.format(
                "id: %s, name: %s, description: %s, quantity state: %s, product state: %s, product category: %s",
                productId, productName, description, quantityState, productState, productCategory);
    }
}
