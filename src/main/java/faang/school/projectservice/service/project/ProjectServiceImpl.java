package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.kafka.ProjectViewEvent;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.kafka.producer.ProjectViewProducer;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final UserContext userContext;
    private final ProjectViewProducer projectViewProducer;

    @Override
    @Transactional
    public ProjectDto create(long requesterId, CreateProjectDto createProjectDto) {
        log.info("create project requested: requesterId={}", requesterId);

        long userId = userContext.getUserId();
        validateUserId(userId, requesterId);

        validateString(createProjectDto.name(), "name");
        validateString(createProjectDto.description(), "description");

        String name = createProjectDto.name().trim();
        String description = createProjectDto.description().trim();

        validateNameUniqueForOwner(requesterId, name);

        Project project = mapper.toProject(createProjectDto);
        project.setOwnerId(requesterId);
        project.setName(name);
        project.setDescription(description);
        project.setStatus(ProjectStatus.CREATED);

        project = projectRepository.save(project);
        log.info("project created: id={}, ownerId={}", project.getId(), project.getOwnerId());

        return mapper.toProjectDto(project);
    }

    @Override
    @Transactional
    public ProjectDto update(long requesterId, long projectId, UpdateProjectDto updateProjectDto) {
        log.info("update project requested: requesterId={}, projectId={}", requesterId, projectId);

        long userId = userContext.getUserId();
        validateUserId(userId, requesterId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("project fetch failed: not found, id={}", projectId);
                    return new EntityNotFoundException("Project not found: id=" + projectId);
                });

        ensureOwner(project, requesterId);

        if (updateProjectDto.name() != null) {
            String trimmed = updateProjectDto.name().trim();
            validateString(trimmed, "name");

            if (!trimmed.equals(project.getName())) {
                validateNameUniqueForOwner(project.getOwnerId(), trimmed);
                project.setName(trimmed);
            }
        }

        if (updateProjectDto.description() != null) {
            String trimmed = updateProjectDto.description().trim();
            validateString(trimmed, "description");
            project.setDescription(trimmed);
        }

        if (updateProjectDto.status() != null) {
            project.setStatus(updateProjectDto.status());
        }

        if (updateProjectDto.visibility() != null) {
            project.setVisibility(updateProjectDto.visibility());
        }

        Project saved = projectRepository.save(project);
        log.info("project updated: id={}", saved.getId());

        return mapper.toProjectDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getById(long requesterId, long projectId) {
        log.info("get project requested: requesterId={}, projectId={}", requesterId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("project fetch failed: not found, id={}", projectId);
                    return new EntityNotFoundException("Project not found: id=" + projectId);
                });

        if (project.getVisibility() == ProjectVisibility.PRIVATE && !isOwner(project, requesterId)) {
            log.warn("access denied to private project: projectId={}, requesterId={}", projectId, requesterId);
            throw new ForbiddenException("You don't have access to this private project");
        }

        if (project.getOwnerId() != requesterId) {
            projectViewProducer.sendToKafka(new ProjectViewEvent(projectId, requesterId));
        }
        return mapper.toProjectDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getAll(long requesterId) {
        log.info("get all projects requested: requesterId={}", requesterId);

        List<Project> all = projectRepository.findAll();

        return all.stream()
                .filter(p -> p.getVisibility() == ProjectVisibility.PUBLIC || isOwner(p, requesterId))
                .map(mapper::toProjectDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> search(long requesterId, String name, ProjectStatus status) {
        log.info("search projects requested: requesterId={}, nameFilter='{}', statusFilter={}",
                requesterId, name, status);

        String nameFilter = StringUtils.trimToNull(name);

        List<Project> source = projectRepository.findAll();

        return source.stream()
                .filter(p -> nameFilter == null || StringUtils.containsIgnoreCase(p.getName(), nameFilter))
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> p.getVisibility() == ProjectVisibility.PUBLIC || isOwner(p, requesterId))
                .map(mapper::toProjectDto)
                .collect(Collectors.toList());
    }


    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            log.warn("validation failed: {} is blank", paramName);
            throw new DataValidationException(paramName + " should be present");
        }
    }

    private void validateUserId(long userId, long requesterId) {
        if (userId != requesterId) {
            log.warn("userId validation failed: contextUserId={} != requesterId={}", userId, requesterId);
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner");
        }
    }

    private void validateNameUniqueForOwner(long ownerId, String name) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            log.warn("project name validation failed: already exists for ownerId={}, name='{}'", ownerId, name);
            throw new DataValidationException("You already have a project with this name");
        }
    }

    private void ensureOwner(Project project, long requesterId) {
        if (!isOwner(project, requesterId)) {
            log.warn("update forbidden: projectId={}, requesterId={}", project.getId(), requesterId);
            throw new ForbiddenException("You are not allowed to modify this project");
        }
    }

    private boolean isOwner(Project project, long userId) {
        return Objects.equals(project.getOwnerId(), userId);
    }
}
