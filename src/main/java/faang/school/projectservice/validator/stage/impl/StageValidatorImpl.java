package faang.school.projectservice.validator.stage.impl;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.stage.StageValidator;
import faang.school.projectservice.validator.task.TaskValidator;
import faang.school.projectservice.validator.teammember.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StageValidatorImpl implements StageValidator {
    private final TeamMemberValidator teamMemberValidator;
    private final StageRepository stageRepository;
    private final ProjectValidator projectValidator;
    private final TaskValidator taskValidator;

    @Override
    public void validateCreation(NewStageDto stageDto) {
        projectValidator.validateProjectExistence(stageDto.getProjectId());
        validateTasksExistence(stageDto);
        validateExecutorsExistence(stageDto);
    }

    @Override
    public Stage validateStageExistence(long id) {
        Optional<Stage> optional = stageRepository.findById(id);
        return optional.orElseThrow(() -> {
            String message = String.format("a stage with %d does not exist", id);

            return new DataValidationException(message);
        });
    }

    @Override
    public void validateStageForToMigrateExistence(Long stageToMigrateId) {
        if (ObjectUtils.isEmpty(stageToMigrateId)) {
            var message = "a stage does not exist";

            throw new DataValidationException(message);
        }

        validateStageExistence(stageToMigrateId);
    }


    private void validateTasksExistence(NewStageDto stageDto) {
        stageDto.getTasksIds()
                .forEach(taskValidator::validateTaskExistence);
    }

    private void validateExecutorsExistence(NewStageDto stageDto) {
        stageDto.getExecutorsIds()
                .forEach(teamMemberValidator::validateTeamMemberExistence);
    }
}
