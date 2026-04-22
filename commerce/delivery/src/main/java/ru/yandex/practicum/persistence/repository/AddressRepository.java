package ru.yandex.practicum.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.persistence.entity.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

}
