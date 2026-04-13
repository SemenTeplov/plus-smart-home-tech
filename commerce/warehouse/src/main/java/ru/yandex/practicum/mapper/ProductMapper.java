package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.entity.Dimension;

@Mapper
public interface ProductMapper {
    @Mapping(target = "dimension", qualifiedByName = "toDimension")
    Product toProduct(NewProductInWarehouseRequest request);

    @Named("toDimension")
    default Dimension toDimension(NewProductInWarehouseRequest request) {
        if (request == null) {
            return null;
        }

        return Dimension.builder()
                .depth(request.dimension().depth())
                .height(request.dimension().height())
                .width(request.dimension().width())
                .build();
    }
}
