package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import feign.Param;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StageRepository {
    private final StageJpaRepository jpaRepository;

    public Stage save(Stage stage) {
        return jpaRepository.save(stage);
    }

    public void delete(Stage stage) {
        jpaRepository.delete(stage);
    }

    public Stage getById(Long stageId) {
        return jpaRepository.findById(stageId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Stage not found by id: %s", stageId))
        );
    }

    public List<Stage> findAll() {
        return jpaRepository.findAll();
    }

    public List<Stage> findAllStagesByProjectId(Long projectId) {
        return jpaRepository.findAllStagesByProjectId(projectId);
    }

    public List<Stage> findStagesByProjectAndFilters(
            Long projectId,
            List<TeamRole> roles,
            List<TaskStatus> taskStatuses
    ) {
        return jpaRepository.findStagesByProjectAndFilters(projectId, roles, taskStatuses);
    }

    public void deleteAllTasksByStageId(Long stageId) {
        jpaRepository.deleteAllTasksByStageId(stageId);
    }

    public void closeTasksByStageId(Long stageId) {
        jpaRepository.closeTasksByStageId(stageId);
    }

    public void reassignTasksFromToStage(Long providerStageId, Stage consumerStage) {
        jpaRepository.reassignTasksFromToStage(providerStageId, consumerStage);
    }


}
