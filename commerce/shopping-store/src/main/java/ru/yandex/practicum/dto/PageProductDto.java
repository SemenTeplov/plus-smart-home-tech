package ru.yandex.practicum.dto;

import lombok.Builder;

import org.springframework.data.domain.Pageable;

import ru.yandex.practicum.persistence.entity.Product;

import java.util.List;

@Builder
public record PageProductDto(
        Long totalElements,
        Integer totalPages,
        Boolean first,
        Boolean last,
        Integer size,
        List<Product> content,
        Integer number,
        List<SortObject> sort,
        Integer numberOfElements,
        Pageable pageable,
        Boolean empty
) {
}
