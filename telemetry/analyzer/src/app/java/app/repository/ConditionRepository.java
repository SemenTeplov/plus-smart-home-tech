package app.java.app.repository;

import app.java.app.model.Condition;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {

}
