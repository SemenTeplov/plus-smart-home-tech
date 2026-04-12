package ru.yandex.practicum.dto;

import lombok.Builder;

@Builder
public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
