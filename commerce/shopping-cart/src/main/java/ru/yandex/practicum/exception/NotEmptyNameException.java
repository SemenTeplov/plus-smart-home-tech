package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotEmptyNameException extends RuntimeException {
    public NotEmptyNameException() {
        super(Message.EXCEPTION_NAME_IS_NOT_EMPTY);
    }
}
