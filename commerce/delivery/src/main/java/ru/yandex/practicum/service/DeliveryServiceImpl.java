package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.DeliveryMapping;
import ru.yandex.practicum.persistence.entity.Delivery;
import ru.yandex.practicum.persistence.repository.DeliveryRepository;
import ru.yandex.practicum.status.DeliveryState;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapping deliveryMapping;

    private final OrderClient orderClient;

    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto planDelivery(DeliveryDto dto) {

        log.info(Message.SAVE_DELIVERY, dto);

        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(dto.orderId(), dto.deliveryId()));

        return deliveryMapping.deliveryToDeliveryDto(
                deliveryRepository.save(deliveryMapping.deliveryDtoToDelivery(dto)));
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID id) {

        log.info(Message.SUCCESSFUL_DELIVERY, id);

        orderClient.delivery(id.toString());

        Delivery delivery = deliveryRepository.findById(id).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID id) {

        log.info(Message.GET_DELIVERY, id);

        Delivery delivery = deliveryRepository.findById(id).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID id) {

        log.info(Message.FAILED_DELIVERY, id);

        orderClient.deliveryFailed(id.toString());

        Delivery delivery = deliveryRepository.findById(id).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.FAILED);
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {

        log.info(Message.CALCULATE_DELIVERY, orderDto);

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId()).orElseThrow(NotFoundException::new);

        double sum = 5.0;
        double way = !delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet()) ? sum *= 2 : sum;
        double fragile = orderDto.getFragile() ? sum *= 0.2 : 0;
        double weight = orderDto.getDeliveryWeight() * 0.3;
        double volume = orderDto.getDeliveryVolume() * 0.2;

        return sum + way + fragile + weight + volume;
    }
}
