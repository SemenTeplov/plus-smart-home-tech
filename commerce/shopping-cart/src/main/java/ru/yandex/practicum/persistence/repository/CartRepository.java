package ru.yandex.practicum.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.persistence.entity.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> getCartByUsername(String username);
}
