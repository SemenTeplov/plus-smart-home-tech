package app.constants;

public class Messages {
    public static final String MESSAGE_SEND_SENSOR = "Пришел запрос на отправку сигнала от сенсора {}";
    public static final String MESSAGE_SEND_HUB = "Пришел запрос на отправку сигнала от хаба {}";
    public static final String SENT_MESSAGE_SUCCESSFUL = "{} сообщение отправлено успешно. Topic: {}, Partition: {}, Offset: {}";
    public static final String SENT_MESSAGE_FAILED = "Сообщение {} с событием {} не было отправлено";
}
