package faang.school.projectservice.serivce.project.stage.remove;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static faang.school.projectservice.exception.ExceptionMessages.MIGRATE_STAGE_ID_IS_REQUIRED;

@Component
public class RemoveWithTasksMigration extends RemoveStrategyExecutor {
    public RemoveWithTasksMigration(StageRepository stageRepository, RemoveStrategy strategyType) {
        super(stageRepository, strategyType);
    }

    @Override
    public void execute(Stage stage, RemoveTypeDto removeTypeDto) {
        if (removeTypeDto.stageForMigrateId() == null) {
            throw new DataValidationException(MIGRATE_STAGE_ID_IS_REQUIRED.getMessage());
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
