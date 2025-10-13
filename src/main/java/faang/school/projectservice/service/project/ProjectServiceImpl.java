package faang.school.projectservice.service.project;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final UserContext userContext;

    @Override
    public ProjectDto create(long requesterId, CreateProjectDto createProjectDto) {
        log.info("create project requested: userId={}", requesterId);
        long userId = userContext.getUserId();

        validateUserId(userId, requesterId);
        validateString(createProjectDto.name(), "title");
        validateName(requesterId, createProjectDto.name());

        Project project = mapper.toProject(createProjectDto);
        project = projectRepository.save(project);
        log.info("Project {} created", project.getId());

        return mapper.toProjectDto(project);
    }

    @Override
    public ProjectDto update(long requesterId, long projectId, UpdateProjectDto updateProjectDto) {
        log.info("create project requested: userId={}, projectId={}", requesterId, projectId);
        long userId = userContext.getUserId();

        validateUserId(userId, requesterId);
        validateNotNull(updateProjectDto.name(), "title");
        validateNotNull(updateProjectDto.status(), "status");
        validateNotNull(updateProjectDto.visibility(), "isPrivate");

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("project fetch failed: not found");
                    return new NotFoundException("Project not found: id=" + projectId);
                });

        Project savedProject = projectRepository.save(project);
        log.info("project {} updated", savedProject.getId());

        return mapper.toProjectDto(savedProject);
    }

    @Override
    public ProjectDto getById(long requesterId, long projectId) {
        log.info("get project requested: userId={}, projectId={}", requesterId, projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("project fetch failed: not found");
                    return new NotFoundException("Project not found: id=" + projectId);
                });

        if (project.getVisibility() == ProjectVisibility.PRIVATE && project.getOwnerId() != requesterId) {
            throw new RuntimeException("You don't have access to this private project");
        }

        return mapper.toProjectDto(project);
    }

    @Override
    public List<ProjectDto> getAll(long requesterId) {
        log.info("get all projects requested: userId={}, projectId={}", requesterId);
        List<Project> all = projectRepository.findAll();

        return all.stream()
                .filter(p -> p.getVisibility() == ProjectVisibility.PUBLIC || p.getOwnerId() == requesterId)
                .map(mapper::toProjectDto)
                .collect(Collectors.toList());
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            log.warn("project name validation failed: is empty");
            throw new RuntimeException(paramName + " should be present!");
        }
    }

    private void validateUserId(long userId, long requesterId) {
        if (userId != requesterId) {
            log.warn("userId validation failed: doest match profile");
            throw new RuntimeException("User " + requesterId + " doesn't match profile owner!");
        }
    }

    private void validateName(long userId, String title) {
        if (projectRepository.existsByOwnerIdAndName(userId, title.trim())) {
            log.warn("project name validation failed: already exists");
            throw new RuntimeException("You already have a project with this name");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            log.warn("Validation failed: missing " + paramName);
            throw new RuntimeException(paramName + " should be present!");
        }
    }
}
