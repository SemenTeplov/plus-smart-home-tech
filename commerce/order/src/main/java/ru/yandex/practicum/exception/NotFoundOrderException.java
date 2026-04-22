package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotFoundOrderException extends RuntimeException {
    public NotFoundOrderException() {
        super(Message.EXCEPTION_NOT_FOUND_ORDER);
    }
}
