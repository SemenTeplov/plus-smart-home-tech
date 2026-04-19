package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.EntityExistException;
import ru.yandex.practicum.exception.NotEnoughQuantityException;
import ru.yandex.practicum.exception.NotInformationAboutProductException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.repository.ProductRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {

        log.info(Message.GET_NEW_PRODUCT, request.productId());

        if (productRepository.findById(request.productId()).isPresent()) {
            throw new EntityExistException();
        }

        productRepository.save(productMapper.toProduct(request));
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {

        log.info(Message.GET_CHECK_PRODUCTS, shoppingCartDto.products());

        List<Product> products = productRepository.findAllById(shoppingCartDto.products().keySet());

        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;


        for (Product product : products) {
            if (shoppingCartDto.products().get(product.getProductId()) <= product.getQuantity()) {
                product.setQuantity(shoppingCartDto.products().get(product.getProductId()));
            } else {
                throw new NotEnoughQuantityException();
            }

            deliveryWeight += product.getWeight();
            deliveryVolume += product.getDimension().getDepth()
                    * product.getDimension().getHeight()
                    * product.getDimension().getWidth();

            if (product.getFragile()) {
                fragile = true;
            }
        }

        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {

        log.info(Message.GET_ADD_PRODUCT, request.productId());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(NotInformationAboutProductException::new);
        product.setQuantity(product.getQuantity() == null ? 0 : product.getQuantity() + request.quantity());

        productRepository.save(product);
    }

    @Override
    public AddressDto getWarehouseAddress() {

        log.info(Message.GET_ADDRESS);

        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
