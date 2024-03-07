package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectViewEventPublisher publisher;
    private final UserServiceClient userServiceClient;
    private final ProjectMapper projectMapper;
    private final List<Filter<Project, ProjectFilterDto>> filters;
    private final ProjectValidator projectValidator;
    private final UserContext userContext;


    public ProjectDto create(ProjectDto projectDto) {
        Long parentId = projectDto.getParentId();
        Project project = preCreate(projectDto);

        if (parentId != null) {
            Project parentProject = getProjectById(parentId);
            project.setParentProject(parentProject);
            projectValidator.validateVisibility(project);
        }

        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    private Project preCreate(ProjectDto projectDto) {
        projectValidator.validateToCreate(projectDto);
        userServiceClient.getUser(projectDto.getOwnerId()); //throws if user doesn't exist

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));
        log.info("Project with ID {} was saved by user with ID {}", savedProject.getId(), savedProject.getOwnerId());
        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = getProjectById(projectDto.getId()); //throws if project doesn't exist
        projectValidator.validateAccessToProject(project.getOwnerId());

        ProjectStatus updatedStatus = projectDto.getStatus();
        String updatedDescription = projectDto.getDescription();
        List<Project> children = project.getChildren();

        if (updatedStatus != null) {
            projectValidator.validateStatus(children, updatedStatus);
            project.setStatus(updatedStatus);
        }
        if (updatedDescription != null) {
            projectValidator.validateDescription(updatedDescription);
            project.setDescription(updatedDescription);
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Project with ID {} was updated by user with ID {}", updatedProject.getId(), updatedProject.getOwnerId());
        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getProjectDtoById(long id) {
        Project projectById = getProjectById(id);
        Long ownerId = projectById.getOwnerId();
        projectValidator.validateAccessToProject(ownerId);

        publisher.publish(
                ProjectViewEvent.builder()
                        .projectId(id)
                        .ownerId(ownerId)
                        .receivedAt(LocalDateTime.now())
                        .build()
        );
        log.info("Project with ID {} was  viewed by user with ID {}", id, ownerId);
        return projectMapper.toDto(projectById);
    }

    public List<ProjectDto> getAll() {
        return projectMapper.entitiesToDtos(getVisibleProjects());
    }

    public List<ProjectDto> getAll(ProjectFilterDto filterDto) {
        Stream<Project> projects = getVisibleProjects().stream();
        List<Project> filteredProjects = getFilteredProjects(filterDto, projects);

        return projectMapper.entitiesToDtos(filteredProjects);
    }

    private List<Project> getFilteredProjects(ProjectFilterDto filterDto, Stream<Project> projects) {
        return filters.stream()
                .filter(prjFilter -> prjFilter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, prjFilter)
                                -> prjFilter.apply(stream, filterDto),
                        Stream::concat)
                .toList();
    }

    private List<Project> getVisibleProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        projectValidator.haveAccessToProject(project.getOwnerId()))
                .toList();
    }

    public Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    public boolean existsProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public List<ProjectDto> getAllSubprojectsByFilter(long parentId, ProjectFilterDto filterDto) {
        Stream<Project> allChildren = getProjectById(parentId).getChildren().stream();
        List<Project> filteredSubProjects = getFilteredProjects(filterDto, allChildren);

        return projectMapper.entitiesToDtos(filteredSubProjects);
    }
}