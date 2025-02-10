package faang.school.projectservice.service.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberValidator {

    private final ProjectRepository projectRepository;

    public void validateLeaderOrOwner(TeamMember member) {
        if (member == null || member.getRoles() == null ||
                member.getRoles().stream().noneMatch(role -> role == TeamRole.OWNER || role == TeamRole.TEAMLEAD)) {
            throw new DataValidationException("Пользователь не является владельцем или тимлидом проекта!");
        }
    }

    public void validateAlreadyInProject(Long memberId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект не существует"));

        boolean isMemberAlreadyInProject = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getUserId().equals(memberId));

        if (isMemberAlreadyInProject) {
            throw new BusinessException("Пользователь уже является членом проекта!");
        }
    }

    public boolean isMemberLeader(TeamMember member) {
        return member == null || member.getRoles().stream().anyMatch(role -> role.equals(TeamRole.TEAMLEAD));
    }

    public boolean isMemberOwner(TeamMember member) {
        return member == null || member.getRoles().stream().anyMatch(role -> role.equals(TeamRole.OWNER));
    }

    public void isTeamMemberExist(TeamMember member) {
        if (member == null) {
            throw new EntityNotFoundException("Участник не найден");
        }
    }
}
