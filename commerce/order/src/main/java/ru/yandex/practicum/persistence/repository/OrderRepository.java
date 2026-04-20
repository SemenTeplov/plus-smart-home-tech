package ru.yandex.practicum.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.persistence.entity.Order;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> getOrderByName(String username);

    Optional<Order> getOrderByCartId(String shoppingCartId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = PAID""")
    Optional<Order> getPaidOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = PAYMENT_FAILED""")
    Optional<Order> getPaymentFailedOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = DELIVERED""")
    Optional<Order> getDeliveredOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = DELIVERY_FAILED""")
    Optional<Order> getDeliveredFailedOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = COMPLETED""")
    Optional<Order> getCompletedOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = ASSEMBLED""")
    Optional<Order> getAssembledOrderByOrderId(@Param("id") String uuid);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM orders
            WHERE order_id = :id AND state = PRODUCT_RETURNED""")
    Optional<Order> getAssemblyFiledOrderByOrderId(@Param("id") String uuid);
}
