package faang.school.projectservice.deletestrategy;

import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeleteWithTasks extends DeleteStrategyExecutor {
    public DeleteWithTasks(StageRepository stageRepository) {
        super(stageRepository, DeleteStrategy.CASCADE_DELETE);
    }

    @Override
    public void execute(Stage stage, DeleteTypeDto deleteTypeDto) {
        stageRepository.delete(stage);
        log.info("{} tasks removed from stage: {}", stage.getTasks().size(), stage.getStageId());
    }
}