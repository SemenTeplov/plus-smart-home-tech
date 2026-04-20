package ru.yandex.practicum.client;

import jakarta.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.dto.OrderDto;

@FeignClient(name = "order")
public interface OrderClient {

    @PostMapping("/delivery/failed")
    ResponseEntity<OrderDto> deliveryFailed(@Valid @RequestBody String uuid);

    @PostMapping("/delivery")
    ResponseEntity<OrderDto> delivery(@Valid @RequestBody String uuid);

    @PostMapping("/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@Valid @RequestBody String uuid);

    @PostMapping("/payment")
    ResponseEntity<OrderDto> payment(@Valid @RequestBody String uuid);
}
