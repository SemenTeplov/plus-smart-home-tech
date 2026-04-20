package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.persistence.entity.Address;
import ru.yandex.practicum.persistence.entity.Delivery;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryMapping {

    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddress")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddress")
    @Mapping(target = "deliveryState", expression = "java(ru.yandex.practicum.persistence.status.DeliveryState.CREATED)")
    Delivery deliveryDtoToDelivery(DeliveryDto dto);

    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddressDto")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddressDto")
    DeliveryDto deliveryToDeliveryDto(Delivery delivery);

    Address addressDtoToAddress(AddressDto dto);

    AddressDto deliveryToAddressDto(Address address);

    @Named("toAddress")
    default Address toAddress(AddressDto dto) {
        if (dto == null) {
            return null;
        }

        return addressDtoToAddress(dto);
    }

    @Named("toAddressDto")
    default AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }

        return deliveryToAddressDto(address);
    }
}
