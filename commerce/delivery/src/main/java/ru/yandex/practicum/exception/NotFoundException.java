package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super(Message.EXCEPTION_NOT_FOUND);
    }
}
