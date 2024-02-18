package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectViewEventPublisher publisher;
    private final UserServiceClient userServiceClient;
    private final ProjectMapper projectMapper;
    private final List<Filter<Project, ProjectFilterDto>> filters;
    private final ProjectValidator projectValidator;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.validateToCreate(projectDto);
        userServiceClient.getUser(projectDto.getOwnerId()); //throws if user doesn't exist

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = getProjectById(projectDto.getId()); //throws if project doesn't exist
        projectValidator.validateAccessToProject(project.getOwnerId());

        ProjectStatus updatedStatus = projectDto.getStatus();
        String updatedDescription = projectDto.getDescription();

        if (updatedStatus != null) {
            project.setStatus(updatedStatus);
        }
        if (updatedDescription != null) {
            projectValidator.validateDescription(updatedDescription);
            project.setDescription(updatedDescription);
        }

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getById(long id) {
        Project projectById = getProjectById(id);
        Long ownerId = projectById.getOwnerId();
        projectValidator.validateAccessToProject(ownerId);
        ProjectViewEvent projectViewEvent = ProjectViewEvent.builder()
                .projectId(id)
                .ownerId(ownerId)
                .build();
        String str = "id " + id+ "owner " + ownerId;
        publisher.publish(str);
        return projectMapper.toDto(projectById);
    }

    public List<ProjectDto> getAll() {
        return projectMapper.entitiesToDtos(getVisibleProjects());
    }

    public List<ProjectDto> getAll(ProjectFilterDto filterDto) {
        Stream<Project> projects = getVisibleProjects().stream();

        List<Project> filteredProjects = filters.stream()
                .filter(prjFilter -> prjFilter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, prjFilter)
                                -> prjFilter.apply(stream, filterDto),
                        Stream::concat)
                .toList();

        return projectMapper.entitiesToDtos(filteredProjects);
    }

    private List<Project> getVisibleProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        projectValidator.haveAccessToProject(project.getOwnerId()))
                .toList();
    }

    private Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    public boolean existProjectById(long projectsId) {
        return projectRepository.existsById(projectsId);
    }
}