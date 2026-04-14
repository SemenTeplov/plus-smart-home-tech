package ru.yandex.practicum.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.persistence.entity.Product;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM products
            WHERE product_category = :category""")
    Page<Product> getProducts(@Param("category") String category, Pageable pageable);
}
