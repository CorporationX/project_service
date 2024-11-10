package faang.school.projectservice.deletestrategy;

import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.exception.StageIdRequiredException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeleteWithTasksMigration extends DeleteStrategyExecutor {
    public DeleteWithTasksMigration(StageRepository stageRepository) {
        super(stageRepository, DeleteStrategy.MOVE_TASKS);
    }

    @Override
    public void execute(Stage stage, DeleteTypeDto deleteTypeDto) {
        if (deleteTypeDto.getStageForMigrateId() == null) {
            throw new StageIdRequiredException();
        }
        Stage stageForMigrate = stageRepository.getById(deleteTypeDto.getStageForMigrateId());
        stageForMigrate.getTasks().addAll(stage.getTasks());
        stageRepository.save(stageForMigrate);
        stageRepository.delete(stage);
        log.info("{} tasks migrated to stage {} from stage {}",
                stage.getTasks().size(), stageForMigrate.getStageId(), stage.getStageId());
    }
}