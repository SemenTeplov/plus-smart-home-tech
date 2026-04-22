package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.NotInformationException;
import ru.yandex.practicum.mapper.PayMapper;
import ru.yandex.practicum.persistence.entity.Pay;
import ru.yandex.practicum.persistence.repository.PayRepository;
import ru.yandex.practicum.persistence.status.PayStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ShoppingStoreClient shoppingStoreClient;

    private final OrderClient orderClient;

    private final PayRepository payRepository;

    private final PayMapper payMapper;

    @Override
    public PaymentDto payment(OrderDto orderDto) {

        log.info(Message.PAY_ORDER, orderDto.getOrderId());

        BigDecimal totalPayment = BigDecimal.valueOf(0.0);

        for (var item : orderDto.getProducts().entrySet()) {
            ProductDto productDto = shoppingStoreClient.getProduct(item.getKey()).getBody();

            if (productDto == null || productDto.getPrice() == null) {
                throw new NotInformationException();
            }

            totalPayment = totalPayment.add(productDto.getPrice().multiply(BigDecimal.valueOf(item.getValue())));
        }

        return PaymentDto.builder()
                .paymentId(orderDto.getPaymentId())
                .totalPayment(totalPayment)
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(totalPayment.add(orderDto.getDeliveryPrice()))
                .build();
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {

        log.info(Message.PREPARED_COST_ORDER, orderDto.getOrderId());

        checkOrder(orderDto.getTotalPrice(), orderDto.getDeliveryPrice());

        BigDecimal Nds = orderDto.getTotalPrice().multiply( BigDecimal.valueOf(0.1));

        return orderDto.getTotalPrice().add(Nds).add(orderDto.getDeliveryPrice());
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID uuid) {

        log.info(Message.SUCCESS_ORDER, uuid);

        orderClient.payment(uuid.toString());

        Pay pay = payRepository.findById(uuid).orElseThrow(NotFoundException::new);
        pay.setStatus(PayStatus.SUCCESS);
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {

        log.info(Message.COST_ORDER, orderDto.getOrderId());

        checkOrder(orderDto.getTotalPrice(), orderDto.getDeliveryPrice());
        payRepository.save(payMapper.orderDtoToPay(orderDto));

        return orderDto.getTotalPrice().add(orderDto.getDeliveryPrice());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID uuid) {

        log.info(Message.FAIL_ORDER, uuid);

        orderClient.paymentFailed(uuid.toString());

        Pay pay = payRepository.findById(uuid).orElseThrow(NotFoundException::new);
        pay.setStatus(PayStatus.FAILED);
    }

    private void checkOrder(BigDecimal totalPrice, BigDecimal deliveryPrice) {
        if (totalPrice == null || deliveryPrice == null) {
            throw new NotInformationException();
        }
    }
}
