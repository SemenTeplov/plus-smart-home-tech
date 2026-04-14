package ru.yandex.practicum.persistence.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wares")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    UUID productId;

    Boolean fragile;

    @Embedded
    Dimension dimension;

    Double weight;

    Long quantity = 0L;
}
