package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotInformationException extends RuntimeException {
    public NotInformationException() {
        super(Message.EXCEPTION_NOT_INFORMATION);
    }
}
