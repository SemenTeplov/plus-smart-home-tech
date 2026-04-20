package ru.yandex.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String PAY_ORDER = "Получил запрос на формирование оплаты для заказа {}";
    public static final String PREPARED_COST_ORDER = "Получил запрос на формирование расчета стоимости для заказа {}";
    public static final String SUCCESS_ORDER = "Получил запрос на эмуляцию успешной оплаты для заказа {}";
    public static final String COST_ORDER = "Получил запрос на формирования стоимости товара для заказа {}";
    public static final String FAIL_ORDER = "Получил запрос на эмуляцию отказа оплаты для заказа {}";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
    public static final String EXCEPTION_NOT_INFORMATION = "Недостаточно информации в заказе для расчёта.";
    public static final String EXCEPTION_NOT_FOUND = "Заказ не найден.";
}
