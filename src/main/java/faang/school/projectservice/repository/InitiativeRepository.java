package faang.school.projectservice.repository;

import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Long> {
    boolean existsByNameAndProjectId(String name, Long projectId);
    boolean existsByProjectIdAndStatus(Long projectId, InitiativeStatus status);
}
