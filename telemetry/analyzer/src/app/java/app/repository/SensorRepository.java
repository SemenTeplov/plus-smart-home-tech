package app.java.app.repository;

import app.java.app.model.Sensor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Collection;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    @Query(nativeQuery = true, value = """
            SELECT s.*
            FROM sensors s
            JOIN scenario_actions sa ON sa.sensor_id = s.id
            WHERE sa.action_id = :id""")
    Optional<Sensor> findByActionId(@Param("id") Long actionId);
}
