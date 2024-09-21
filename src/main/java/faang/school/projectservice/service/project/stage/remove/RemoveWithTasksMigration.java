package faang.school.projectservice.service.project.stage.remove;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static faang.school.projectservice.exception.ExceptionMessages.MIGRATE_STAGE_ID_IS_REQUIRED;
import static faang.school.projectservice.exception.ExceptionMessages.TASKS_NOT_FOUND;

@Component
public class RemoveWithTasksMigration extends RemoveStrategyExecutor {
    public RemoveWithTasksMigration(StageRepository stageRepository, RemoveStrategy strategyType) {
        super(stageRepository, strategyType);
    }

    @Override
    @Transactional
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        if (removeTypeDto.stageForMigrateId() == null) {
            throw new DataValidationException(MIGRATE_STAGE_ID_IS_REQUIRED.getMessage());
        }
        if (stage.getTasks() == null) {
            throw new EntityNotFoundException(TASKS_NOT_FOUND.getMessage().formatted(stage.getStageId()));
        }
        Stage stageForMigrate = stageRepository.getById(removeTypeDto.stageForMigrateId());
        if (stageForMigrate.getTasks() == null) {
            stageForMigrate.setTasks(new ArrayList<>());
        }
        stageForMigrate.getTasks().addAll(stage.getTasks());
        stageRepository.save(stageForMigrate);
        stageRepository.delete(stage);
    }
}
