package faang.school.projectservice.service.project.stage.removestrategy;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RemoveWithTasks extends RemoveStrategyExecutor {
    public RemoveWithTasks(StageRepository stageRepository) {
        super(stageRepository, RemoveStrategy.CASCADE_DELETE);
    }

    @Override
    @Transactional
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        stageRepository.delete(stage);
        log.info("{} tasks removed from stage: {}", stage.getTasks().size(), stage.getStageId());
    }
}
