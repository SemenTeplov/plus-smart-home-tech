package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotEmptyProductException extends RuntimeException {
    public NotEmptyProductException() {
        super(Message.EXCEPTION_PRODUCT_IS_NOT_EMPTY);
    }
}
