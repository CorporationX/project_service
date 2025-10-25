package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.exception.project.ResourceNotFoundException;
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
import java.util.function.Predicate;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Project create(ProjectCreateDto projectCreateDto, Long ownerId) {
        log.info("Creating a project by a user {}", ownerId);

        ProjectValidator.validateUniqueProjectNameForOwner(
                projectCreateDto.name(),
                ownerId,
                () -> projectRepository.existsByOwnerIdAndName(ownerId, projectCreateDto.name())
        );

        Project project = ProjectMapper.toEntity(projectCreateDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);

        projectRepository.save(project);
        log.info("Project {} successfully created with ID={}", project.getName(), project.getId());

        return project;
    }

    @Transactional
    public Project update(Long id, ProjectUpdateDto projectUpdateDto, Long ownerId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ProjectValidator.validateAccessToProject(project, ownerId);
        ProjectValidator.validateUpdate(project,
                projectUpdateDto.status() != null ? projectUpdateDto.status() : null,
                projectUpdateDto.description());

        if (projectUpdateDto.status() != null) {
            project.setStatus(projectUpdateDto.status());
        }
        if (projectUpdateDto.description() != null) {
            project.setDescription(projectUpdateDto.description());
        }

        project.setUpdatedAt(LocalDateTime.now());
        log.info("Project '{}' (id={}) has been updated", project.getName(), project.getId());
        return projectRepository.save(project);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getProjectsByFilter(String name, ProjectStatus status, Long userId) {
        Predicate<Project> matchesName = project -> name == null || project.getName().contains(name);
        Predicate<Project> matchesStatus = project -> status == null || project.getStatus() == status;
        Predicate<Project> isVisible = project ->
                project.getVisibility() != ProjectVisibility.PRIVATE ||
                        ProjectValidator.isUserParticipant(project, userId);

        return projectRepository.findAll().stream()
                .filter(matchesName.and(matchesStatus).and(isVisible))
                .map(ProjectMapper::toDto)
                .toList();
    }

    public Project getProjectById(Long id, Long userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        ProjectValidator.validateAccessToProject(project, userId);
        return project;
    }
}