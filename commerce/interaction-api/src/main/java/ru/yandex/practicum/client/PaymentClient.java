package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

@FeignClient(name = "payment")
public interface PaymentClient {

    @PostMapping("/productCost")
    ResponseEntity<Double> productCost(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping
    ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto);
}
