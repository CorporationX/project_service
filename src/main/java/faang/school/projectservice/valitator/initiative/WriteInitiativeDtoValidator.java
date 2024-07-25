package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        checkNoExistenceActiveInitiative(writeInitiativeDto);
        checkRelevantRole(writeInitiativeDto);
    }

    private void checkRelevantRole(WriteInitiativeDto writeInitiativeDto) {
        TeamMember teamMember = teamMemberService.findById(writeInitiativeDto.getCuratorId());
        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            log.error("WriteInitiativeDtoValidator.checkRelevantRole: No relevant Role {} : {}", TeamRole.OWNER, teamMember.getRoles());
            throw new ConflictException(String.format("No relevant Role %s : %s", TeamRole.OWNER, teamMember.getRoles()));
        }
    }

    private void checkDescriptionIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getDescription() == null) {
            log.error("WriteInitiativeDtoValidator.checkDescriptionIsNotNull: Description cannot be null");
            throw new IllegalArgumentException("Description cannot be null");
        }
    }

    private void checkNameIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getName() == null) {
            log.error("WriteInitiativeDtoValidator.checkNameIsNotNull: Name cannot be null");
            throw new IllegalArgumentException("Name cannot be null");
        }
    }

    private void checkProjectIsNotNull(WriteInitiativeDto writeInitiativeDto) {
        if (writeInitiativeDto.getProjectId() == null) {
            log.error("WriteInitiativeDtoValidator.checkProjectIsNotNull: Project cannot be null");
            throw new IllegalArgumentException("Project id cannot be null");
        }
    }

    private void checkNoExistenceActiveInitiative(WriteInitiativeDto writeInitiativeDto) {
        Project project = projectService.findById(writeInitiativeDto.getProjectId());
        for (Initiative initiative : project.getInitiatives()) {
            if (InitiativeStatus.isActiveInitiative(initiative)) {
                log.error("WriteInitiativeDtoValidator.checkNoExistenceActiveInitiative: Project with id [] has an active initiative", project.getId());
                new ConflictException("Project with id " + project.getId() + " has an active initiative");
            }
        }
    }
}
