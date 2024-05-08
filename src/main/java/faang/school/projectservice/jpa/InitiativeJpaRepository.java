package faang.school.projectservice.jpa;

import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitiativeJpaRepository extends JpaRepository<Initiative, Long> {
}
