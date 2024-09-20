package faang.school.projectservice.serivce.project.stage.remove;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.springframework.stereotype.Component;

@Component
public class RemoveWithTasks extends RemoveStrategyExecutor {
    public RemoveWithTasks(StageRepository stageRepository, RemoveStrategy strategyType) {
        super(stageRepository, strategyType);
    }

    @Override
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        stageRepository.delete(stage);
    }
}
