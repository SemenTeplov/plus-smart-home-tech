package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NotExistNameException;
import ru.yandex.practicum.exception.NotFoundOrderException;
import ru.yandex.practicum.mapping.OrderMapping;
import ru.yandex.practicum.persistence.entity.Order;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.repository.OrderRepository;
import ru.yandex.practicum.status.DeliveryState;
import ru.yandex.practicum.status.State;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapping orderMapping;

    private final DeliveryClient deliveryClient;

    private final PaymentClient paymentClient;

    private final WarehouseClient warehouseClient;

    @Override
    public OrderDto getClientOrders(String username) {

        if (username == null || username.isBlank()) {
            throw new NotExistNameException();
        }

        log.info(Message.GET_ORDER, username);

        return orderMapping.toOrderDto(orderRepository.getOrderByName(username).orElseThrow());
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {

        log.info(Message.CREATE_ORDER, request.shoppingCart().products());

        Order order = Order.builder()
                .shoppingCartId(request.shoppingCart().shoppingCartId()).build();

        for (Map.Entry<UUID, Long> eProduct : request.shoppingCart().products().entrySet()) {
            Product product = Product.builder().id(eProduct.getKey()).countProducts(eProduct.getValue()).build();
            order.addProduct(product);
        }

        order = orderRepository.save(order);

        order.setDeliveryId(Objects.requireNonNull(deliveryClient.planDelivery(DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(request.deliveryAddress())
                .toAddress(warehouseClient.getWarehouseAddress().getBody())
                .deliveryState(DeliveryState.CREATED)
                .build()).getBody()).deliveryId());

        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(order.getProducts().stream()
                        .collect(Collectors.toMap(Product::getId, Product::getCountProducts)), order.getOrderId())).getBody();

        order.setDeliveryWeight(bookedProductsDto.deliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.deliveryVolume());
        order.setFragile(bookedProductsDto.fragile());

        PaymentDto paymentDto = paymentClient.payment(orderMapping.toOrderDto(order)).getBody();

        order.setTotalPrice(paymentDto.totalPayment());
        order.setDeliveryPrice(paymentDto.deliveryTotal());
        order.setProductPrice(paymentDto.feeTotal());
        order.setPaymentId(paymentDto.paymentId());

        order = orderRepository.save(order);

        return orderMapping.toOrderDto(order);
    }

    @Override
    public ShoppingCartDto productReturn(ShoppingCartDto shoppingCartDto) {

        log.info(Message.RETURNED_ORDER, shoppingCartDto.products());

        Order order = orderRepository.getOrderByCartId(shoppingCartDto.shoppingCartId().toString())
                .orElseThrow(NotFoundOrderException::new);

        order.setProducts(order.getProducts().stream()
                .peek(p -> p.setCountProducts(shoppingCartDto.products().get(p.getId())))
                .collect(Collectors.toSet()));
        order.setState(State.PRODUCT_RETURNED);

        orderRepository.save(order);

        return new ShoppingCartDto(shoppingCartDto.shoppingCartId(),
                order.getProducts().stream().collect(Collectors.toMap(Product::getId, Product::getCountProducts)));
    }

    @Override
    public OrderDto payment(String uuid) {

        log.info(Message.PAID_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getPaidOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto paymentFailed(String uuid) {

        log.info(Message.FAILED_PAID_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getPaymentFailedOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto delivery(String uuid) {

        log.info(Message.DELIVERY_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getDeliveredOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto deliveryFailed(String uuid) {

        log.info(Message.FAILED_DELIVERY_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getDeliveredFailedOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto complete(String uuid) {

        log.info(Message.COMPLETE_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getCompletedOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto calculateTotalCost(String uuid) {

        log.info(Message.TOTAL_COAST_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.findById(UUID.fromString(uuid))
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto calculateDeliveryCost(String uuid) {

        log.info(Message.DELIVERY_COAST_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.findById(UUID.fromString(uuid))
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto assembly(String uuid) {

        log.info(Message.ASSEMBLY_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getAssembledOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }

    @Override
    public OrderDto assemblyFailed(String uuid) {

        log.info(Message.FAILED_ASSEMBLY_ORDER, uuid);

        return orderMapping.toOrderDto(orderRepository.getAssemblyFiledOrderByOrderId(uuid)
                .orElseThrow(NotFoundOrderException::new));
    }
}
