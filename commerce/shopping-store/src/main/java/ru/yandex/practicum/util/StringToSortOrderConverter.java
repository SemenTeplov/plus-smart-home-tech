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

        parts[0] = parts[0]
                .replace("I", "_i")
                .replace("C", "_c")
                .replace("N", "_n")
                .replace("S", "_s");

        if (parts.length == 1) {
            return Sort.Order.by(parts[0]);
        }

        if (parts[1].equalsIgnoreCase("desc")) {
            return Sort.Order.desc(parts[0]);
        }

        return Sort.Order.asc(parts[0]);
    }
}
