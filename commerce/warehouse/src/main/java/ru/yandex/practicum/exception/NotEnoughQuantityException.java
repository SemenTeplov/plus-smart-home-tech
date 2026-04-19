package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotEnoughQuantityException extends RuntimeException {
    public NotEnoughQuantityException() {
        super(Message.EXCEPTION_ENOUGH_PRODUCTS);
    }
}
