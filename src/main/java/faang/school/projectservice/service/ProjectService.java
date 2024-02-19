package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectMapper projectMapper;
    private final List<Filter<Project, ProjectFilterDto>> filters;
    private final ProjectValidator projectValidator;


    public ProjectDto createProject(ProjectDto projectDto) {
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
        return projectMapper.toEntity(projectDto);
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
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
        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getById(long id) {
        Project projectById = getProjectById(id);
        projectValidator.validateAccessToProject(projectById.getOwnerId());
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

    private Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    public boolean existsProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public List<ProjectDto> getAllSubprojectsByFilter(long parentId, ProjectFilterDto projectFilterDto) {
        Stream<Project> allChildren = getProjectById(parentId).getChildren().stream();
        List<Project> filteredSubProjects = getFilteredProjects(projectFilterDto, allChildren);

        return projectMapper.entitiesToDtos(filteredSubProjects);
    }

}