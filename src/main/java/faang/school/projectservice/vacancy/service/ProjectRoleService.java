package faang.school.projectservice.vacancy.service;

import faang.school.projectservice.vacancy.repository.ProjectRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectRoleService {
    private final ProjectRoleRepository projectRoleRepository;

    public boolean isOwnerOrManager(long userId, UUID projectId) {
        System.out.printf("Проверка роли пользователя %s в проекте %s%n", userId, projectId);
        return true;
    }

    public void assignRolesToProject(UUID projectId, Enum<?> role, List<UUID> candidateIds) {
        System.out.printf("Назначаем кандидатам %s роль %s в проекте %s%n",
                candidateIds, role, projectId);
    }

    public boolean isProjectMember(UUID projectId, UUID userId) {
        return projectRoleRepository.existsByProjectIdAndUserId(projectId, userId);
    }
}
