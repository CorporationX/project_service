package faang.school.projectservice.service.project.stage.removestrategy;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static faang.school.projectservice.exception.ExceptionMessages.MIGRATE_STAGE_ID_IS_REQUIRED;

@Slf4j
@Component
public class RemoveWithTasksMigration extends RemoveStrategyExecutor {
    public RemoveWithTasksMigration(StageRepository stageRepository) {
        super(stageRepository, RemoveStrategy.MIGRATE);
    }

    @Override
    @Transactional
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        if (removeTypeDto.stageForMigrateId() == null) {
            throw new DataValidationException(MIGRATE_STAGE_ID_IS_REQUIRED.getMessage());
        }
        Stage stageForMigrate = stageRepository.getById(removeTypeDto.stageForMigrateId());
        stageForMigrate.getTasks().addAll(stage.getTasks());
        stageRepository.save(stageForMigrate);
        stageRepository.delete(stage);
        log.info("{} tasks migrated to stage {} from stage {}",
                stage.getTasks().size(), stageForMigrate.getStageId(), stage.getStageId());
    }
}
