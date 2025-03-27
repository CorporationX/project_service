package faang.school.projectservice.repository;

import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InitiativeRepository extends JpaRepository<Initiative, Long> {
}
