package faang.school.projectservice.validation.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.model.stage.Stage;

public interface StageValidator {

    void validateExistence(long id);

    Stage validateStageExistence(long id);

    void validateStageForToMigrateExistence(Long stageToMigrateId);

    void validateCreation(NewStageDto stageDto);
}
