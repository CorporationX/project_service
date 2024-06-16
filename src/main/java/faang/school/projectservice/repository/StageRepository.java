package faang.school.projectservice.repository;

import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findAllByProjectIdAndStageStatus(long projectId, StageStatus stageStatus);

    List<Stage> findAllByProjectId(long projectId);
}
