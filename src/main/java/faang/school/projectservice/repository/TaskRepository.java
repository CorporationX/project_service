package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.stage.stageId = ?1")
    void deleteAllByStageId(Long stageId);

    List<Task> findAllByStage_StageId(Long stageId);
}
