package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.persistence.entity.Pay;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PayMapper {

    @Mapping(target = "id", source = "paymentId")
    @Mapping(target = "feeTotal", expression = "java(orderDto.getTotalPrice().add(orderDto.getDeliveryPrice()))")
    @Mapping(target = "status", expression = "java(ru.yandex.practicum.persistence.status.PayStatus.PENDING)")
    Pay orderDtoToPay(OrderDto orderDto);
}
