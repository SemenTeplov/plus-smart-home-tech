package ru.yandex.practicum.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import ru.yandex.practicum.status.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID deliveryId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "from_address")
    Address fromAddress;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "to_address")
    Address toAddress;

    UUID orderId;

    @Enumerated(EnumType.STRING)
    DeliveryState deliveryState = DeliveryState.CREATED;
}
