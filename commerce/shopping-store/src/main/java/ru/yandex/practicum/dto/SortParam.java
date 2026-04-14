package ru.yandex.practicum.dto;

import org.springframework.data.domain.Sort;

public record SortParam(
        String field,
        Sort.Direction direction
) {
}
