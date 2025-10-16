package faang.school.projectservice.service.stage.deletion;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MoveDeletionStrategy — Стратегия переноса задач в другой этап перед удалением
 *
 * @author bozya
 * @since 06.08.2025
 */
@Slf4j
@Service("moveDeletionStrategy")
@RequiredArgsConstructor
public class MoveTasksDeletionStrategy implements StageDeletionStrategy {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final Long DEFAULT_TARGET_STAGE_ID = 1L;

    @Override
    public void delete(Stage stage) {
        Stage targetStage = stageRepository.findById(DEFAULT_TARGET_STAGE_ID)
                .orElseThrow(() -> new EntityNotFoundException("Стандартный этап не найден"));

        stage.getTasks().forEach(task -> task.setStage(targetStage));
        taskRepository.saveAll(stage.getTasks());
        stageRepository.delete(stage);

        log.info("Перенесено {} задач в этап {} перед тем как удалить этап {}",
                stage.getTasks().size(), targetStage.getStageId(), stage.getStageId());
    }
}