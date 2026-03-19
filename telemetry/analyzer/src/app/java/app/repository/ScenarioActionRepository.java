package app.java.app.repository;

import app.java.app.model.ScenarioAction;
import app.java.app.model.ScenarioActionId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {

}
