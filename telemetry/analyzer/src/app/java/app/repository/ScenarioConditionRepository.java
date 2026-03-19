package app.java.app.repository;

import app.java.app.model.ScenarioCondition;
import app.java.app.model.ScenarioConditionId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

}
