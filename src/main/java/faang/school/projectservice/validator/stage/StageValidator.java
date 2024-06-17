package faang.school.projectservice.validator.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.model.stage.Stage;

public interface StageValidator {
    Stage validateStageExistence(long id);

    void validateStageForToMigrateExistence(Long stageToMigrateId);

    void validateCreation(NewStageDto stageDto);
}
