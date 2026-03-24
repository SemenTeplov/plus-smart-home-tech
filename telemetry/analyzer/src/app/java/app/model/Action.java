package app.java.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "actions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String type;

    Integer value;

    @Override
    public String toString() {
        return String.format("id: %d, type: %s, value: %d", id, type, value);
    }
}
