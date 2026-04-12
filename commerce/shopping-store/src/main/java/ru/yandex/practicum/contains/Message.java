package ru.yandex.practicum.contains;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_PAGES_BY_CATEGORY= "Получение страницу продуктов по category: {}, page {}, size {}, sort: {}";
    public static final String GET_PRODUCT_FOR_SAVE= "Получение продукта для сохранения: {}";
    public static final String GET_PRODUCT_FOR_UPDATE= "Получение продукта для обновления: {}";
    public static final String GET_ID_FOR_DELETE= "Получение id для удаления: {}";
    public static final String GET_STATE_FOR_UPDATE= "Получение состояния для обновления: {}";
    public static final String GET_PRODUCT_BY_ID = "Получение продукта по id: {}";
    public static final String EXCEPTION_NOT_FOUND = "Продукт не найден";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
}
