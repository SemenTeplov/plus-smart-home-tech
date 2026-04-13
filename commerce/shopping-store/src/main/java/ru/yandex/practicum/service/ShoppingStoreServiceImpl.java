package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.contains.Message;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.mapper.ProductMapper;
import ru.yandex.practicum.persistence.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public Page<Product> getProducts(String category, Integer page, Integer size, List<Sort.Order> sort) {
        log.info(Message.GET_PAGES_BY_CATEGORY, category, page, size, sort);

        Sort order = Sort.by(sort);
        PageRequest pageable = PageRequest.of(page, size, order);

        return productRepository.getProducts(category, pageable);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info(Message.GET_PRODUCT_FOR_SAVE, productDto);

        Product product = productMapper.productDtoToProduct(productDto);

        log.info("Продукт {}", product);

        return productMapper
                .productToProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info(Message.GET_PRODUCT_FOR_UPDATE, productDto);

        Product product = productRepository.findById(UUID.fromString(productDto.getProductId()))
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        productMapper.updateProduct(product, productDto);

        return productDto;
    }

    @Override
    public Boolean removeProductFromStore(String uuid) {
        log.info(Message.GET_ID_FOR_DELETE, uuid);

        Product product = productRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        productRepository.delete(product);

        return true;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info(Message.GET_STATE_FOR_UPDATE, setProductQuantityStateRequest);

        Product product = productRepository.findById(UUID.fromString(setProductQuantityStateRequest.productId()))
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));
        product.setQuantityState(setProductQuantityStateRequest.quantityState());

        return true;
    }

    @Override
    public ProductDto getProduct(String productId, String uuid) {
        log.info(Message.GET_PRODUCT_BY_ID, uuid);

        return productMapper.productToProductDto(productRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND)));
    }
}
