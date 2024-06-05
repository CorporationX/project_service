package faang.school.projectservice.validator.stage.impl;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.stage.StageValidator;
import faang.school.projectservice.validator.task.TaskValidator;
import faang.school.projectservice.validator.teammember.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StageValidatorImpl implements StageValidator {
    private final TeamMemberValidator teamMemberValidator;
    private final StageJpaRepository stageJpaRepository;
    private final ProjectValidator projectValidator;
    private final TaskValidator taskValidator;

    @Override
    public Stage validateStageBeforeCreation(Stage stageEntity, NewStageDto stageDto) {
        Project projectEntity = projectValidator.validateProjectExistence(stageDto.getProjectId());
        List<Task> taskEntities = validateTasksExistence(stageDto);
        List<TeamMember> executors = validateExecutorsExistence(stageDto);

        stageEntity.setProject(projectEntity);
        stageEntity.setTasks(taskEntities);
        stageEntity.setExecutors(executors);

        return stageEntity;
    }

    @Override
    public Stage validateStageExistence(long id) {
        var optional = stageJpaRepository.findById(id);
        return optional.orElseThrow(() -> {
            var message = String.format("a stage with %d does not exist", id);

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


    private List<Task> validateTasksExistence(NewStageDto stageDto) {
        return stageDto.getTasksIds()
                .stream()
                .map(taskValidator::validateTaskExistence)
                .toList();
    }

    private List<TeamMember> validateExecutorsExistence(NewStageDto stageDto) {
        return stageDto.getExecutorsIds()
                .stream()
                .map(teamMemberValidator::validateTeamMemberExistence)
                .toList();
    }
}
