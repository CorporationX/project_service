package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * ProjectServiceImpl — описание класса.
 * <p>
 * * Реализация методов бизнес-логики из ProjectService
 * </p>*
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final static int DESCRIPTION_LENGTH = 4096;

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> filters;
    private final UserContext userContext;

    @Override
    public ProjectDto createProject(ProjectDto projectDto) {
        validateProject(projectDto);

        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        validateId(projectDto);
        validateFields(projectDto);

        Project existingProject = getExistingProject(projectDto.getId());

        applyUpdates(existingProject, projectDto);

        Project updatedProject = projectRepository.save(existingProject);

        return projectMapper.toDto(updatedProject);
    }

    @Override
    public List<ProjectDto> getProjectsByFilter(ProjectDto projectDto) {
        Stream<Project> filteredProjects = projectRepository.findAll().stream();

        for (ProjectFilter projectFilter : filters) {
            if (projectFilter.isApplicable(projectDto)) {
                filteredProjects = projectFilter.apply(filteredProjects, projectDto);
            }
        }

        return filteredProjects
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        Long currentUserId = userContext.getUserId();

        return projectRepository.findAll().stream()
                .filter(project -> canUserSeeProject(project, currentUserId))
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = findProjectByIdOrThrow(id);
        validateUserAccess(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project);
        projectRepository.deleteById(projectId);
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ValidationException("проект с таким id не найден"));
    }

    private void checkAccess(Project project) {
        Long currentUserId = userContext.getUserId();
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ValidationException("нет доступа на удаление проекта");
        }
    }

    private Project findProjectByIdOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("проект не найден"));
    }

    private void validateUserAccess(Project project) {
        Long currentUserId = userContext.getUserId();
        if (!canUserSeeProject(project, currentUserId)) {
            throw new ValidationException("нет доступа к проекту с данным id");
        }
    }

    private boolean canUserSeeProject(Project project, Long currentUserId) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {

            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(user -> user.getId().equals(currentUserId));
        }
        return true;
    }

    private void validateProject(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new ValidationException("Название проекта не может быть пустым");
        }

        if (projectDto.getOwnerId() == null) {
            throw new ValidationException("Не указан владелец проекта");
        }

        boolean exists = projectRepository.existsByOwnerIdAndName(
                projectDto.getOwnerId(),
                projectDto.getName()
        );

        if (exists) {
            throw new ValidationException("Проект с таким именем уже существует");
        }
    }

    private void validateId(ProjectDto projectDto) {
        if (projectDto.getId() == null) {
            throw new ValidationException("Id проекта обязателен для обновления");
        }
    }

    private void validateFields(ProjectDto projectDto) {
        if (projectDto.getDescription() == null && projectDto.getStatus() == null) {
            throw new ValidationException("Нечего обновлять: нужно указать статус или описание");
        }
        if (projectDto.getDescription() != null && projectDto.getDescription().length() > DESCRIPTION_LENGTH) {
            throw new ValidationException("Описание слишком длинное");
        }
    }

    private Project getExistingProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("проект не найден"));
    }

    private void applyUpdates(Project existingProject, ProjectDto projectDto) {
        if (projectDto.getDescription() != null) {
            existingProject.setDescription(projectDto.getDescription());
        }

        if (projectDto.getStatus() != null) {
            boolean isValid = Arrays.stream(ProjectStatus.values())
                    .anyMatch(s -> s.name().equals(projectDto.getStatus()));
            if (!isValid) {
                throw new ValidationException("Некорректный статус проекта: " + projectDto.getStatus());
            }

            existingProject.setStatus(ProjectStatus.valueOf(projectDto.getStatus()));
        }
    }
}