package faang.school.projectservice.model.taskActionAfterDeletingStage;

import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskActionDelete extends TaskActionAfterDeletingStageAbstract {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter) {
        stageRepository.deleteAllTasksByStageId(stageId);
        logAction("All tasks was deleted");
    }
}
