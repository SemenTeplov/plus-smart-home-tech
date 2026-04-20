package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

@FeignClient(name = "delivery")
public interface DeliveryClient {

    @PutMapping
    ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto dto);

    @PostMapping("/cost")
    ResponseEntity<Double> deliveryCost(@RequestBody OrderDto orderDto);
}
