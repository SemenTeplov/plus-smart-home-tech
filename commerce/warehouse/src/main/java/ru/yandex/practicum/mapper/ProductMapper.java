package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dto.DimensionDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.entity.Dimension;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    @Mapping(target = "dimension", qualifiedByName = "toDimension")
    Product toProduct(NewProductInWarehouseRequest request);

    @Named("toDimension")
    default Dimension toDimension(DimensionDto dto) {
        if (dto == null) {
            return null;
        }

        return Dimension.builder()
                .depth(dto.depth())
                .height(dto.height())
                .width(dto.width())
                .build();
    }
}
