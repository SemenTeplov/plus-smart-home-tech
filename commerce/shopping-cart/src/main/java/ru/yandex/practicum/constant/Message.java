package ru.yandex.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_CART= "Получение корзины пользователя {}";
    public static final String GET_PRODUCTS= "Добавление в корзину пользователя {} товары: {}";
    public static final String DEACTIVATE_CART= "Деактивация корзины пользователя {}";
    public static final String REMOVED_PRODUCTS= "Удаление из корзины пользователя {} товары: {}";
    public static final String CHANGE_PRODUCTS= "Изменение в корзине пользователя {} товары: {}";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
    public static final String EXCEPTION_NAME_IS_NOT_EMPTY = "Имя пользователя не должно быть пустым.";
    public static final String EXCEPTION_PRODUCT_IS_NOT_EMPTY = "Список продуктов не может быть пустым.";
}
