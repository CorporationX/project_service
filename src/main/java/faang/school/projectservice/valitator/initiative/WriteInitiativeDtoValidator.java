package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WriteInitiativeDtoValidator {

    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;

    public void validate(WriteInitiativeDto writeInitiativeDto) {
        checkProjectIsNotNull(writeInitiativeDto);
        checkNameIsNotNull(writeInitiativeDto);
        checkDescriptionIsNotNull(writeInitiativeDto);
        checkDescriptionIsNotNull(writeInitiativeDto);
        checkExistenceActiveInitiative(writeInitiativeDto);
        checkRelevantRole(writeInitiativeDto);
    }

    private void checkRelevantRole(WriteInitiativeDto writeInitiativeDto) {
        TeamMember teamMember = teamMemberService.findById(writeInitiativeDto.getCuratorId());
        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new IllegalArgumentException("No relevant Role");
        }
    }

    private void checkDescriptionIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getDescription() == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
    }

    private void checkNameIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getName() == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
    }

    private void checkProjectIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getProjectId() == null) {
            throw new IllegalArgumentException("Project id cannot be null");
        }
    }

    private void checkExistenceActiveInitiative(WriteInitiativeDto writeInitiativeDto) {
        Project project = projectService.findById(writeInitiativeDto.getProjectId());
        for (Initiative initiative : project.getInitiatives()) {
            if (InitiativeStatus.isActiveInitiative(initiative)) {
                new RuntimeException("Project with id " + project.getId() + " has an active initiative");
            }
        }
    }
}
