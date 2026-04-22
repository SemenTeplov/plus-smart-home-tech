package ru.yandex.practicum.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.persistence.entity.Order;
import ru.yandex.practicum.persistence.entity.Product;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapping {

    @Mapping(target = "products", source = "products", qualifiedByName = "toMap")
    OrderDto toOrderDto(Order order);

    @Named("toMap")
    default Map<UUID, Long> toMap(Set<Product> products) {
        if (products == null) {
            return null;
        }

        return products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getCountProducts));
    }
}
