package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StageValidator {

    private final ProjectRepository projectRepository;

    public void validateStageDtoForProjectCompletedAndCancelled(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        if (project.getStatus().equals(ProjectStatus.COMPLETED) ||
                project.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }
    }

    public void validateNewStageId(SubtaskActionDto subtaskActionDto, Long newStageId) {
        if (subtaskActionDto.equals(SubtaskActionDto.MOVE_TO_NEXT_STAGE) && newStageId == null) {
            throw new DataValidationException("New stage id cannot be null");
        }
    }
}
