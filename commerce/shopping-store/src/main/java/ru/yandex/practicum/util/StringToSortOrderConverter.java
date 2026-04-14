package ru.yandex.practicum.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class StringToSortOrderConverter implements Converter<String, Sort.Order> {

    @Override
    public Sort.Order convert(String source) {
        if (source == null || source.isBlank()) {
            return Sort.Order.by("productId");
        }

        String[] parts = source.split(",");

        if (parts.length == 1) {
            return Sort.Order.by(parts[0]);
        }

        if (parts[1].contains("desc")) {
            return Sort.Order.desc(parts[1]);
        }

        return Sort.Order.asc(parts[0]);
    }
}
