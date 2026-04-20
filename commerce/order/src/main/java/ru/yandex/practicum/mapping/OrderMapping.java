package ru.yandex.practicum.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.persistence.entity.Order;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapping {

    @Mapping(target = "username", ignore = true)
    OrderDto toOrderDto(Order order);
}
