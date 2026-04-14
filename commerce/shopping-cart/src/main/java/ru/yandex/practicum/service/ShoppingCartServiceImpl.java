package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.client.ProductClient;
import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NotEmptyNameException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.persistence.entity.Cart;
import ru.yandex.practicum.persistence.entity.Order;
import ru.yandex.practicum.persistence.repository.CartRepository;
import ru.yandex.practicum.persistence.ststus.CartState;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

        if (username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.GET_CART, username);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.save(Cart.builder().username(username).build()));

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<String, Long> products) {

        if (username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.GET_PRODUCTS, username, products);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.save(Cart.builder().username(username).build()));

        productClient.checkProducts(ShoppingCartDto.builder()
                .shoppingCartId(cart.getId()).products(products).build());

        cart.setOrders(products.entrySet().stream()
                .map(e -> Order.builder().cart(cart).name(e.getKey()).countProducts(e.getValue()).build())
                .collect(Collectors.toList()));

        cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {

        if (username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.DEACTIVATE_CART, username);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.save(Cart.builder().username(username).build()));

        cart.setState(CartState.DEACTIVATE);

        cartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {

        if (username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.REMOVED_PRODUCTS, username, products);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.save(Cart.builder().username(username).build()));

        cart.getOrders().removeIf(order -> products.contains(order.getId()));

        cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest) {

        if (username.isBlank()) {
            throw new NotEmptyNameException();
        }

        log.info(Message.CHANGE_PRODUCTS, username, changeProductQuantityRequest);

        Cart cart = cartRepository.getCartByUsername(username)
                .orElse(cartRepository.save(Cart.builder().username(username).build()));

        for (int i = 0; i < cart.getOrders().size(); i++) {
            if (cart.getOrders().get(i).getId().equals(changeProductQuantityRequest.productId())) {
                cart.getOrders().get(i).setCountProducts(changeProductQuantityRequest.newQuantity());
            }
        }

        cartRepository.save(cart);

        return productMapper.toShoppingCartDto(cart);
    }
}
