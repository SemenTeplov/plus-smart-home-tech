package ru.yandex.practicum.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String GET_NEW_PRODUCT= "Поступил новый продукт: {}";
    public static final String GET_CHECK_PRODUCTS= "Поступил запрос на проверку продуктов: {}";
    public static final String GET_ADD_PRODUCT= "Поступил запрос на добавление продукта {}";
    public static final String GET_ADDRESS= "Поступил запрос на получение адреса";
    public static final String SEND_PRODUCT= "Поступил запрос на отправку заказа {}";
    public static final String RETURN_PRODUCT= "Поступил запрос на возврат заказа {}";
    public static final String PREPARED_PRODUCT= "Поступил запрос на подгатовку к отправке заказа {}";
    public static final String EXCEPTION_WRONG_ARGUMENT = "Некорректные данные";
    public static final String EXCEPTION_INNER_ERROR_SERVER = "Внутренняя ошибка сервера.";
    public static final String EXCEPTION_ENTITY_EXIST = "Ошибка, товар с таким описанием уже зарегистрирован на складе.";
    public static final String EXCEPTION_ENOUGH_PRODUCTS = "Ошибка, товар из корзины не находится в требуемом количестве на складе";
    public static final String EXCEPTION_NOT_INFORMATION = "Нет информации о товаре на складе";
}
