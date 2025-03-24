package faang.school.projectservice.service.Stage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CascadeDeleteStrategy implements StageDeletionStrategy{
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;
    @Override
    public void deleteStage(Stage stage) {
        log.info("Deleting stage {}", stage);
        log.info("Deleting tasks {}", stage.getTasks());
        taskRepository.deleteByStage(stage);
        stageRepository.delete(stage);
    }

    @Override
    public boolean requiresTargetStage() {
        return false;
    }
}
