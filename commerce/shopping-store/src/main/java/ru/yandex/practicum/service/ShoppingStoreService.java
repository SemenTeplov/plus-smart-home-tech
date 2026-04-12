package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.persistence.entity.Product;

import java.util.List;

public interface ShoppingStoreService {

    public Page<Product> getProducts(String category, Integer page, Integer size, List<Sort.Order> sort);

    public ProductDto createNewProduct(ProductDto productDto);

    public ProductDto updateProduct(ProductDto productDto);

    public Boolean removeProductFromStore(String uuid);

    public Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest);

    public ProductDto getProduct(String productId, String uuid);
}
