package faang.school.projectservice.service.stage.deletion;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * CloseDeletionStrategy — Стратегия закрытия всех задач перед удалением этапа
 *
 * @author bozya
 * @since 06.08.2025
 */
@Service("closeDeletionStrategy")
@RequiredArgsConstructor
@Slf4j
public class CloseTaskDeletionStrategy implements StageDeletionStrategy {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void delete(Stage stage) {
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CLOSED);
            task.setStage(null);
        });

        taskRepository.saveAll(stage.getTasks());
        stageRepository.delete(stage);

        log.info("Этап {} удален, удалено {} задач",
                stage.getStageId(), stage.getTasks().size());
    }
}