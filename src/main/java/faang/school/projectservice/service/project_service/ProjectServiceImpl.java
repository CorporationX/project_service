package faang.school.projectservice.service.project_service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ValidatorProject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<ProjectFilters> filters;
    private final ValidatorProject validation;

    public void createProject(ProjectDto projectDto) {
        Project project = validation.getEntity(projectDto);
        validation.validationCreateProject(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
    }

    public void updateStatus(ProjectDto projectDto, ProjectStatus status) {
        Project project = validation.getEntity(projectDto);
        System.out.println(project);
        if (!projectRepository.existsById(project.getId())) {
            throw new NoSuchElementException("The project does not exist");
        }

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public void updateDescription(ProjectDto projectDto, String description) {
        Project project = validation.getEntity(projectDto);

        if (!projectRepository.existsById(project.getId())) {
            throw new NoSuchElementException("The project does not exist");
        }

        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projectStream,
                        (project, filter) -> filter.apply(project, filterDto),
                        (s1, s2) -> s1)
                .map(mapper::toDto)
                .toList();
    }

    public List<ProjectDto> getProjects() {
        return projectRepository.findAll()
                .stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .map(mapper::toDto)
                .toList();
    }

    public ProjectDto findById(long id) {
        return mapper.toDto(validation.findById(id));
    }

    public List<ProjectDto> checkingVisibility(ProjectFilterDto filterDto, TeamMemberDto requester) {
        if (filterDto.getVisibility().equals(ProjectVisibility.PUBLIC)) {
            getProjectsFilters(filterDto);
        }

        return getPrivateProject(filterDto, requester);
    }

    private List<ProjectDto> getPrivateProject(ProjectFilterDto filterDto, TeamMemberDto requester) {
        List<ProjectDto> privateProjects = getProjectsFilters(filterDto);

        for (ProjectDto projectDto : privateProjects) {
            if (projectDto.getTeams().contains(requester.getTeam())) {
                return getProjectsFilters(filterDto);
            }
        }

        return null;
    }
}
