package faang.school.projectservice.validation.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validation.project.ProjectValidator;
import faang.school.projectservice.validation.task.TaskValidator;
import faang.school.projectservice.validation.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageValidatorImpl implements StageValidator {

    private final StageRepository stageRepository;
    private final TeamMemberValidator teamMemberValidator;
    private final ProjectValidator projectValidator;
    private final TaskValidator taskValidator;

    @Override
    public void validateExistence(long id) {
        if (!stageRepository.existsById(id)) {
            throw new NotFoundException("Stage with id=" + id + " does not exist");
        }
    }

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
                .forEach(teamMemberValidator::validateExistence);
    }
}
