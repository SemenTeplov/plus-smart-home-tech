package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.persistence.entity.Cart;
import ru.yandex.practicum.persistence.entity.Order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "shoppingCartId", source = "id")
    @Mapping(target = "products", source = "orders", qualifiedByName = "ordersToMap")
    ShoppingCartDto toShoppingCartDto(Cart cart);

    @Named("ordersToMap")
    default Map<String, Long> ordersToMap(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream().collect(Collectors.toMap(o -> o.getId().toString(), Order::getCountProducts));
    }
}
