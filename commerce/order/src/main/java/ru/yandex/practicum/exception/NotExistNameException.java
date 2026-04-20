package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotExistNameException extends RuntimeException {
    public NotExistNameException() {
        super(Message.EXCEPTION_NOT_EXIST_NAME);
    }
}
