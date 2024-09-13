package faang.school.projectservice.model.taskActionAfterDeletingStage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@AllArgsConstructor
public class TaskActionReassign extends TaskActionAfterDeletingStageAbstract {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long providerStageId, Long consumerStageId) {
        Stage consumerStage = stageRepository.getById(consumerStageId);
        if (consumerStage == null) {
            throw new IllegalArgumentException("Consumer Stage not found by id: " + consumerStageId);
        }

        stageRepository.reassignTasksFromToStage(providerStageId,consumerStage);
        };
    }
