package ru.yandex.practicum.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.persistence.entity.Delivery;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

}
