package ru.yandex.practicum.exception;

import ru.yandex.practicum.constant.Message;

public class EntityExistException extends RuntimeException {
    public EntityExistException() {
        super(Message.EXCEPTION_ENTITY_EXIST);
    }
}
