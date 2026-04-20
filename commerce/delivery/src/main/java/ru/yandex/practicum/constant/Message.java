package ru.yandex.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String SAVE_DELIVERY = "Поступил запрос на сохранение доставки {}";
    public static final String SUCCESSFUL_DELIVERY = "Поступило потверждение об успешной доставки {}";
    public static final String GET_DELIVERY = "Поступило потверждение о поступление в доставку {}";
    public static final String FAILED_DELIVERY = "Поступило потверждение о не успешной доставке {}";
    public static final String CALCULATE_DELIVERY = "Поступило запрос о расчете {}";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_NOT_FOUND = "Заказ не найден.";
}
