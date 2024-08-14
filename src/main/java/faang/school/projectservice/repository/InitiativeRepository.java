package faang.school.projectservice.repository;

import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Long> {

    List<Initiative> findAllByProjectId(Long projectId);
}
