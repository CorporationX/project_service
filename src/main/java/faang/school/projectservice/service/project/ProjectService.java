package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
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

        boolean nameExists = projectRepository.existsByOwnerIdAndName(ownerId, projectCreateDto.name());
        ProjectValidator.validateUniqueProjectNameForOwner(projectCreateDto.name(), ownerId, nameExists);

        Project project = ProjectMapper.toEntity(projectCreateDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);

        projectRepository.save(project);
        log.info("Project {} successfully created with ID={}", project.getName(), project.getId());

        return project;
    }

    @Transactional
    public Project update(Long id, ProjectUpdateDto projectUpdateDto) {
        Project project = projectRepository.getReferenceById(id);

        ProjectValidator.validateUpdate(project,
                ProjectStatus.valueOf(projectUpdateDto.status()),
                projectUpdateDto.description());

        if (projectUpdateDto.status() != null) {
            project.setStatus(ProjectStatus.valueOf(projectUpdateDto.status()));
        }
        if (projectUpdateDto.description() != null) {
            project.setDescription(projectUpdateDto.description());
        }

        projectRepository.save(project);
        log.info("Project '{}' (id={}) has been updated", project.getName(), project.getId());
        return project;
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDto)
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
                .map(ProjectMapper::toDto)
                .toList();
    }

    public Project getProjectById(Long id, Long userId) {
        Project project = projectRepository.getReferenceById(id);
        ProjectValidator.validateAccessToProject(project, userId);
        return project;
    }
}