package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.contains.Message;
import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.SortObject;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.mapper.ProductMapper;
import ru.yandex.practicum.persistence.repository.ProductRepository;
import ru.yandex.practicum.status.ProductState;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public PageProductDto getProducts(String category, Integer page, Integer size, List<Sort.Order> sort) {
        log.info(Message.GET_PAGES_BY_CATEGORY, category, page, size, sort);

        Sort order = Sort.by(sort);
        PageRequest pageable = PageRequest.of(page, size, order);

        Page<Product> products = productRepository.getProducts(category, pageable);

        return PageProductDto.builder()
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .first(products.isFirst())
                .last(products.isLast())
                .size(products.getSize())
                .content(products.getContent())
                .number(products.getNumber())
                .sort(sort.stream()
                        .map(s -> SortObject.builder()
                                .direction(s.getDirection().name())
                                .nullHandling(s.getNullHandling().name())
                                .ascending(s.isAscending())
                                .property(toParameterName(s))
                                .ignoreCase(s.isIgnoreCase()).build())
                        .toList())
                .numberOfElements(products.getNumberOfElements())
                .pageable(products.getPageable())
                .empty(products.isEmpty()).build();
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info(Message.GET_PRODUCT_FOR_SAVE, productDto);

        Product product = productMapper.productDtoToProduct(productDto);

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
    public Boolean removeProductFromStore(UUID uuid) {
        log.info(Message.GET_ID_FOR_DELETE, uuid);

        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));

        product.setProductState(ProductState.DEACTIVATE);

        productRepository.save(product);

        return true;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info(Message.GET_STATE_FOR_UPDATE, setProductQuantityStateRequest);

        Product product = productRepository.findById(UUID.fromString(setProductQuantityStateRequest.productId()))
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND));
        product.setQuantityState(setProductQuantityStateRequest.quantityState());

        productRepository.save(product);

        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info(Message.GET_PRODUCT_BY_ID, productId);

        return productMapper.productToProductDto(productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND)));
    }

    private String toParameterName(Sort.Order s) {
        return !s.getProperty().contains("_")
                ? s.getProperty()
                : s.getProperty().replace(
                s.getProperty().substring(s.getProperty().indexOf("_"), s.getProperty().indexOf("_") + 2),
                String.valueOf(Character.toUpperCase(s.getProperty().charAt(s.getProperty().indexOf("_") + 1))));
    }
}
