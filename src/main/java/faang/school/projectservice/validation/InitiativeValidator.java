package faang.school.projectservice.validation;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitiativeValidator {
    public void validate(InitiativeDto initiative) {
        if (initiative.getProjectId() == null) {
            throw new DataValidationException("initiative projectId must not be null");
        }
        if (initiative.getName() == null) {
            throw new DataValidationException("initiative name must not be null");
        }
        if (initiative.getDescription() == null) {
            throw new DataValidationException("initiative description must not be null");
        }
    }

    public void validateCuratorAndProject(TeamMember curator, Project project) {
        if (!curator.getTeam().getProject().getId().equals(project.getId())) {
            throw new DataValidationException("curator not in the initiative project");
        }
    }
}
