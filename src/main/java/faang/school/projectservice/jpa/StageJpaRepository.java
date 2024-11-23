package faang.school.projectservice.jpa;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageJpaRepository extends JpaRepository<Stage, Long> {

    Optional<List<Stage>> findAllByProjectId(Long projectId);
}
