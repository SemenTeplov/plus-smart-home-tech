package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class NotInformationAboutProductException extends RuntimeException {
    public NotInformationAboutProductException() {
        super(Message.EXCEPTION_NOT_INFORMATION);
    }
}
