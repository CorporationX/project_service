package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitiativeValidator {

    private final InitiativeRepository initiativeRepository;
    private final TeamMemberService teamMemberService;

    public void projectHasNotActiveInitiative(Long projectId) {
        boolean hasActiveInitiatives = initiativeRepository.findAllByProjectId(projectId).stream()
                .anyMatch(initiative -> initiative.getStatus() == InitiativeStatus.OPEN ||
                        initiative.getStatus() == InitiativeStatus.IN_PROGRESS);
        if (hasActiveInitiatives) {
            log.info("Project already has an active initiative. Method: checkProjectActiveInitiative");
            throw new DataValidationException("Project already has an active initiative");
        }
    }

    public void curatorRoleValid(Long curatorId) {
        TeamMember member = teamMemberService.findById(curatorId);
        boolean hasValidRole = member.getRoles().stream()
                .anyMatch(role -> role == TeamRole.ANALYST ||
                        role == TeamRole.MANAGER ||
                        role == TeamRole.OWNER);
        if (!hasValidRole) {
            log.info("The curator does not have the required specialization. Method: checkCuratorRole");
            throw new DataValidationException("The curator does not have the required specialization");
        }
    }

    public void checkAllTasksDone(Initiative initiative) {
        boolean allTasksDone = initiative.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
        if (!allTasksDone) {
            log.info("An attempt to change the status while the stage is active. Method: checkStagesStatusInitiative");
            throw new DataValidationException("You cannot change the status because not all stages have been completed yet");
        }
    }

}
