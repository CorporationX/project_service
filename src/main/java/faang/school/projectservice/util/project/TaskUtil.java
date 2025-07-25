package faang.school.projectservice.util.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TaskUtil {
    private final ProjectRepository repository;
    private final UserContext userContext;

    public boolean isInTeam(Long projectId) {
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));

        boolean isUserInProjectTeam = project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .findFirst()
                        .filter(teamMember -> teamMember.getId() == userContext.getUserId())
                        .isPresent();
        if (!isUserInProjectTeam) {
            log.error("Пользователь id = {} не состоит в команде проекта id = {}",
                    userContext.getUserId(), projectId);
            throw new ForbiddenException("Пользователь не состоит в команде проекта.");
        }
        return true;
    }
}
