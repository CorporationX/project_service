package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final List<ProjectFilter> filters;
    private final ProjectRepository projectRepository;
    private final ResourceService resourceService;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;

    @Override
    public ProjectOutputDto create(ProjectForCreationDto projectDto) {
        validateTitleUniqueness(projectDto);
        ProjectForCreationDto completedDto = setDefaultCreationFields(projectDto);
        Project project = projectMapper.toProjectEntity(completedDto);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectOutputDto update(ProjectForUpdateDto changingProjectDto) {
        Long projectId = changingProjectDto.getId();
        Project existingProject = findProjectById(projectId);
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
        Project project = findProjectById(projectId);
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (!isMemberOfPrivateProject(project)) {
                throw new DataValidationException("All privet projects are visible only for members");
            }
        }
        return projectMapper.toProjectDto(project);
    }

    @Override
    public ProjectOutputDto uploadCoverImage(Long projectId, MultipartFile file) {
        Resource resource = resourceService.uploadResource(file, projectId);
        Project project = resource.getProject();
        project.setCoverImageId(resource.getId().toString());
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectOutputDto deleteCoverImage(Long projectId) {
        Project project = findProjectById(projectId);
        if (project.getCoverImageId() == null) {
            throw new IllegalArgumentException(String.format("There are no cover image for project with id %d", project));
        }
        resourceService.deleteResource(Long.parseLong(project.getCoverImageId()));
        project.setCoverImageId(null);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public byte[] getCoverImage(Long projectId) {
        byte[] bytes;
        Project project = findProjectById(projectId);
        try (InputStream inputStream = resourceService.downloadResource(Long.parseLong(project.getCoverImageId()))) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("IOException was thrown while downloading cover image for project ID {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error while downloading cover image for project ID %d".formatted(projectId), e);
        }
        return bytes;
    }

    private Project findProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project with id %d was not found".formatted(projectId)));
    }

    private boolean isMemberOfPrivateProject(Project project) {
        long userId = userContext.getUserId();
        boolean isTeamMember = Stream.ofNullable(project.getTeams()).flatMap(List::stream)
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId() == userId);
        return (project.getOwnerId() == userId || isTeamMember);
    }

    private void validateTitleUniqueness(ProjectForCreationDto projectDto) {
        long projectOwnerId = userContext.getUserId();
        String projectName = projectDto.getName();
        Optional<List<Project>> sameNamedProjects = projectRepository.findByNameAndOwnerId(projectName, projectOwnerId);
        if (!sameNamedProjects.get().isEmpty() && sameNamedProjects.get().stream()
                .noneMatch(project -> project.getStatus().equals(ProjectStatus.CANCELLED))) {
            throw new DataValidationException(
                    String.format("User with id = %d already has a project named %s", projectOwnerId, projectName));
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
}
