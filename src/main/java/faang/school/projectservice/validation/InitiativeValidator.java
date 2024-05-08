package faang.school.projectservice.validation;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitiativeValidator {
    private final StageRepository stageRepository;

    public void validate(InitiativeDto initiative) {
        if (initiative.getProjectId() == null) {
            throw new DataValidationException("initiative projectId must not be null");
        }
        if (initiative.getName() == null || initiative.getName().isBlank()) {
            throw new DataValidationException("initiative name must not be null");
        }
        if (initiative.getDescription() == null || initiative.getDescription().isBlank()) {
            throw new DataValidationException("initiative description must not be null");
        }
        if (initiative.getCuratorId() == null) {
            throw new DataValidationException("initiative curatorId must not be null");
        }
    }

    public void validateCuratorAndProject(TeamMember curator, Project project) {
        if (!curator.getTeam().getProject().getId().equals(project.getId())) {
            throw new DataValidationException("curator not in the initiative project");
        }
    }

    public void validateClosedInitiative(InitiativeDto initiative) {
        List<Stage> stages = stageRepository.findAll().stream()
                .filter(stage -> initiative.getStageIds().contains(stage.getStageId()))
                .toList();

        boolean areAllClosed = stages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus() == TaskStatus.DONE);

        if (!areAllClosed) {
            throw new DataValidationException("All tasks in all stages must be done to close the initiative");
        }
    }
}
