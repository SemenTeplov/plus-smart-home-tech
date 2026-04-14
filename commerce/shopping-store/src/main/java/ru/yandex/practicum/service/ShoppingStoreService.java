package ru.yandex.practicum.service;

import org.springframework.data.domain.Sort;

import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    public PageProductDto getProducts(String category, Integer page, Integer size, List<Sort.Order> sort);

    public ProductDto createNewProduct(ProductDto productDto);

    public ProductDto updateProduct(ProductDto productDto);

    public Boolean removeProductFromStore(UUID uuid);

    public Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest);

    public ProductDto getProduct(UUID productId);
}
