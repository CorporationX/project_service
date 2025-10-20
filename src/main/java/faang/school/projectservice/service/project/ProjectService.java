package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto, Long ownerId) {
        log.info("Создание проекта пользователем {}", ownerId);

        projectValidator.validateUniqueProjectNameForOwner(projectDto.name(), ownerId);

        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        Project saved = projectRepository.save(project);
        log.info("Проект {} успешно создан c ID={}", saved.getName(), saved.getId());
        return projectMapper.toDto(saved);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectValidator.validateUpdate(project, dto.status(), dto.description());

        Optional.ofNullable(dto.status()).ifPresent(project::setStatus);
        Optional.ofNullable(dto.description()).ifPresent(project::setDescription);

        project.setUpdatedAt(LocalDateTime.now());

        Project saved = projectRepository.save(project);
        log.info("Проект '{}' (id={}) обновлён", saved.getName(), saved.getId());
        return projectMapper.toDto(saved);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getProjectsByFilter(String name, ProjectStatus status, Long userId) {
        Predicate<Project> matchesName = project -> name == null || project.getName().contains(name);
        Predicate<Project> matchesStatus = project -> status == null || project.getStatus() == status;
        Predicate<Project> isVisible = project -> {
            if (project.getVisibility() == null || project.getVisibility() != ProjectVisibility.PRIVATE) {
                return true;
            }
            return project.getTeams() != null && project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(member -> member.getId().equals(userId));
        };

        return projectRepository.findAll().stream()
                .filter(matchesName.and(matchesStatus).and(isVisible))
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id, Long userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectValidator.validateAccessToProject(project, userId);
        return projectMapper.toDto(project);
    }
}