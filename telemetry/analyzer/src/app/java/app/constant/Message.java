package app.java.app.constant;

public class Message {
    public static String OBJECT_NAME = "Объектом является {}";
    public static String CHECK_PARAMETER = "Проверка {}";
    public static String SEND_REQUEST = "Отправлен DeviceActionRequest: HubId - {}, ScenarioName - {}, SensorId - {}," +
            "TypeValue - {}, Value - {}";
    public static String GET_SENSORS_SNAPSHOT = "Поступил SensorsSnapshotAvro с HubId: {}";
    public static String GET_HUB_EVENT = "Поступил HubEventAvro с HubId: {}";
    public static String HUB_EVENT_NAME = "HubEventAvro является {}";
    public static String WORK_SCENARIO_CONDITION = "Обработка ScenarioCondition: Type - {}, Operation - {}, Value - {}";
    public static String WORK_SCENARIO_ACTION = "Обработка ScenarioAction: Type - {}, Value - {}";
    public static String CREATED_SCENARIO_CONDITION = "Был создан ScenarioCondition: {}";
    public static String CREATED_SCENARIO_ACTION = "Был создан ScenarioAction: {}";
    public static String GET_SENSOR = "Получен Sensor: {}";
    public static String GET_SCENARIO = "Получен Scenario: {}";
    public static String GET_CONDITION = "Получен Condition {}";
    public static String GET_ACTION = "Получен Action: {}";
}
