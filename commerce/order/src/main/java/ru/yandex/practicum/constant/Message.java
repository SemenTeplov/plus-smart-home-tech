package ru.yandex.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_ORDER = "Поступил запрос на получение заказа пользователем {}";
    public static final String CREATE_ORDER = "Поступил запрос на создание заказа {}";
    public static final String RETURNED_ORDER = "Поступил запрос на возвра заказа {}";
    public static final String PAID_ORDER = "Поступил запрос на предоставление оплаченного заказа {}";
    public static final String FAILED_PAID_ORDER = "Поступил запрос на предоставление заказа с проваленной оплатой {}";
    public static final String SUCCESS_PAID_ORDER = "Поступил запрос на предоставление заказа с успешной оплатой {}";
    public static final String DELIVERY_ORDER = "Поступил запрос на предоставление доставленного заказа {}";
    public static final String FAILED_DELIVERY_ORDER = "Поступил запрос на предоставление заказа с проваленной доставкой {}";
    public static final String COMPLETE_ORDER = "Поступил запрос на предоставление завершенного заказа {}";
    public static final String TOTAL_COAST_ORDER = "Поступил запрос на предоставление заказа с предоставленным счетом {}";
    public static final String DELIVERY_COAST_ORDER = "Поступил запрос на предоставление заказа с предоставленным счетом за доставку {}";
    public static final String ASSEMBLY_ORDER = "Поступил запрос на предоставление собранного заказа {}";
    public static final String FAILED_ASSEMBLY_ORDER = "Поступил запрос на предоставление заказа с проваленной сборкой {}";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_NOT_EXIST_NAME = "Имя пользователя не должно быть пустым";
    public static final String EXCEPTION_NOT_FOUND_ORDER = "Не найден заказ";
}
