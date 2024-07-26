package faang.school.projectservice.validation;

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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitiativeValidator {

    private final InitiativeRepository initiativeRepository;
    private final TeamMemberService teamMemberService;

    public boolean checkProjectActiveInitiative(Long projectId) {
        List<Initiative> initiatives = initiativeRepository.findAll();
        return initiatives.stream()
                .filter(initiative -> initiative.getProject().getId().equals(projectId))
                .anyMatch(initiative -> initiative.getStatus() != InitiativeStatus.CLOSED
                        && initiative.getStatus() != InitiativeStatus.DONE);
    }

    public boolean checkCuratorRole(Long curatorId) {
        TeamMember member = teamMemberService.findById(curatorId);
        return member.getRoles().contains(TeamRole.ANALYST)
                || member.getRoles().contains(TeamRole.MANAGER);
    }

    public boolean checkStagesStatusInitiative(Initiative initiative) {
        return initiative
                .getStages()
                .stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
    }

}
