package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.client.ProductClient;
import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NotEmptyNameException;
import ru.yandex.practicum.exception.NotEmptyProductException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.persistence.entity.Cart;
import ru.yandex.practicum.persistence.entity.Order;
import ru.yandex.practicum.persistence.repository.CartRepository;
import ru.yandex.practicum.persistence.ststus.CartState;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartRepository cartRepository;

    private final ProductMapper productMapper;

    private final ProductClient productClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {

        if (username == null || username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.GET_CART, username);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElseGet(() -> {
                    log.info("Не найден {}", username);
                    return cartRepository.saveAndFlush(Cart.builder().username(username).build());
                });

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<String, Long> products) {

        if (username == null || username.isBlank()) {
            throw new NotEmptyNameException();
        }

        if (products == null || products.isEmpty()) {
            throw new NotEmptyProductException();
        }

        log.info(Message.GET_PRODUCTS, username, products);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElseGet(() -> cartRepository.saveAndFlush(Cart.builder().username(username).build()));

        try {
            productClient.checkProductQuantityEnoughForShoppingCart(ShoppingCartDto.builder()
                    .shoppingCartId(cart.getId()).products(products).build());
        } catch (Exception e) {
            log.error("productClient выбросил исключение {}", e.getMessage());
        }

        Map<UUID, Order> existingOrdersMap = cart.getOrders().stream()
                .collect(Collectors.toMap(Order::getId, Function.identity()));

        for (var entry : products.entrySet()) {

            UUID id = UUID.fromString(entry.getKey());
            Long quantity = entry.getValue();
            Order order = existingOrdersMap.get(id);

            if (order != null) {
                order.setCountProducts(order.getCountProducts() + quantity);
            } else {
                Order newOrder = Order.builder()
                        .id(id)
                        .cart(cart)
                        .countProducts(quantity)
                        .build();
                cart.addOrder(newOrder);
            }
        }

        cart = cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {

        if (username == null || username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.DEACTIVATE_CART, username);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.saveAndFlush(Cart.builder().username(username).build()));

        cart.setState(CartState.DEACTIVATE);

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {

        if (username == null || username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.REMOVED_PRODUCTS, username, products);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElseThrow(NotEmptyProductException::new);

        List<Order> orderListForRemove = cart.getOrders().stream()
                .filter(o -> products.contains(o.getId())).toList();

        cart.getOrders().removeAll(orderListForRemove);

        cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest) {

        if (username == null || username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.CHANGE_PRODUCTS, username, changeProductQuantityRequest);

        Cart cart = cartRepository.getCartByUsername(username).orElseThrow(NotEmptyProductException::new);

        Order order = cart.getOrders().stream()
                .filter(o -> o.getId().equals(changeProductQuantityRequest.productId()))
                .findFirst().orElseThrow(NotEmptyProductException::new);

        order.setCountProducts(order.getCountProducts() + changeProductQuantityRequest.newQuantity());

        cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }
}
