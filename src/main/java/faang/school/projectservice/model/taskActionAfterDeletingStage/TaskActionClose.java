package faang.school.projectservice.model.taskActionAfterDeletingStage;

import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskActionClose extends TaskActionAfterDeletingStageAbstract {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long stageId, Long unusedParameter ) {
        stageRepository.closeTasksByStageId(stageId);
        logAction("All tasks was closed");
    }
}
