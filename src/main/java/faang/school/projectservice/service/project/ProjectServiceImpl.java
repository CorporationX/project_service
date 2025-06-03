package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final static long DEFAULT_START_STORAGE_SIZE = 0;

    private final List<ProjectFilter> filters;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;

    @Value("${entity.project.default-storage-size}")
    private long maxStorageSize;

    @Override
    public ProjectOutputDto create(ProjectForCreationDto projectDto) {
        validateTitleUniqueness(projectDto);
        ProjectForCreationDto completedDto = setDefaultCreationFields(projectDto);
        Project project = projectMapper.toProjectEntity(completedDto);
        project.setMaxStorageSize(convertToBigInt(maxStorageSize));
        project.setStorageSize(convertToBigInt(DEFAULT_START_STORAGE_SIZE));

        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectOutputDto update(ProjectForUpdateDto changingProjectDto) {
        Long projectId = changingProjectDto.getId();
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException
                        ("No project with id %d has been found".formatted(projectId)));
        Long ownerId = existingProject.getOwnerId();
        long userId = userContext.getUserId();
        if (ownerId != userId) {
            throw new DataValidationException("Projects can be changed only be theirs owners");
        }
        projectMapper.update(changingProjectDto, existingProject);
        existingProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existingProject);
        return projectMapper.toProjectDto(existingProject);
    }

    @Override
    public List<ProjectOutputDto> getFilteredProjects(ProjectFilterDto dto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        projects = projects
                .filter(project -> {
                    if (project.getVisibility() == ProjectVisibility.PRIVATE) {
                        return isMemberOfPrivateProject(project);
                    }
                    return true;
                });
        for (ProjectFilter filter : filters) {
            if (filter.isApplicable(dto)) {
                projects = filter.apply(projects, dto);
            }
        }
        return projects.map(projectMapper::toProjectDto).toList();
    }

    @Override
    public ProjectOutputDto getProjectById(long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No project with this id has been found"));
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (!isMemberOfPrivateProject(project)) {
                throw new DataValidationException("All privet projects are visible only for members");
            }
        }
        return projectMapper.toProjectDto(project);
    }

    private boolean isMemberOfPrivateProject(Project project) {
        long userId = userContext.getUserId();
        boolean isTeamMember = Stream.ofNullable(project.getTeams()).flatMap(List::stream)
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId() == userId);
        return (project.getOwnerId() == userId || isTeamMember);
    }

    private void validateTitleUniqueness(ProjectForCreationDto projectDto) {
        long projectOwnerId = userContext.getUserId();
        String projectName = projectDto.getName();
        List<Project> sameNamedProjects = projectRepository.findByNameAndOwnerId(projectName, projectOwnerId);
        if (!sameNamedProjects.isEmpty()) {
            if (sameNamedProjects.stream()
                    .noneMatch(project -> project.getStatus().equals(ProjectStatus.CANCELLED))) {
                throw new DataValidationException(
                        String.format("User with id = %d already has a project named %s", projectOwnerId, projectName));
            }
        }
    }

    private ProjectForCreationDto setDefaultCreationFields(ProjectForCreationDto dto) {
        if (dto.getOwnerId() == null) {
            dto.setOwnerId(userContext.getUserId());
        }
        if (dto.getVisibility() == null) {
            dto.setVisibility(ProjectVisibility.PUBLIC);
        }
        dto.setStatus(ProjectStatus.CREATED);
        return dto;
    }

    public BigInteger convertToBigInt(long value) {
        return BigInteger.valueOf(DataSize.ofGigabytes(value).toBytes());
    }
}
