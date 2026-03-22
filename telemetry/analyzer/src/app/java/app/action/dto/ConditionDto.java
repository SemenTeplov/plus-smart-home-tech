package app.java.app.action.dto;

import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.Sensor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionDto {
        Sensor sensor;
        Scenario scenario;
        Condition condition;

        @Override
        public String toString() {
                return String.format("%s, %s, %s", sensor, scenario, condition);
        }
}
