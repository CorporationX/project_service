package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final String ALL_SUBPROJECTS_DONE_MOMENT_NAME = "All subprojects completed";
    //TODO: Доделать тесты
    private final ProjectRepository projectRepository;
    private final MomentService momentService;
    private final ProjectMapper mapper;
    private final ProjectValidator validator;
    private final List<ProjectFilter> filters;

    public ProjectDto getProjectById(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return mapper.toDto(project);
    }

    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        validator.verifyCanBeCreated(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        Project projectToBeCreated = mapper.toModel(projectDto);
        fillProject(projectToBeCreated, projectDto);

        Project saved = projectRepository.save(projectToBeCreated);
        return mapper.toDto(saved);
    }

@Transactional
    public ProjectDto update(ProjectDto projectDto) {
        validator.verifyCanBeUpdated(projectDto);

        Project projectToBeUpdated = mapper.toModel(projectDto);
        manageFinishedProject(projectToBeUpdated);
        manageVisibilityChange(projectToBeUpdated);

        Project saved = projectRepository.save(projectToBeUpdated);
        fillProject(saved, projectDto);
        return mapper.toDto(saved);
    }

    public ProjectDto getById(Long id) {
        Project project = projectRepository.getProjectById(id);

        return mapper.toDto(project);
    }

    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();

        return mapper.toDto(projects);
    }

    public List<ProjectDto> search(ProjectFilterDto filter) {
        List<Project> projects = projectRepository.findAll();

        return filterProjects(filter, projects);
    }

    private List<ProjectDto> filterProjects(ProjectFilterDto filter, List<Project> projects) {
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(projects.stream(), filter))
                .distinct()
                .map(mapper::toDto)
                .toList();
    }

    public List<ProjectDto> searchSubprojects(Long parentProjectId, ProjectFilterDto filter) {
        List<Project> projects = projectRepository.getProjectById(parentProjectId).getChildren();

        return filterProjects(filter, projects);
    }

    private void manageVisibilityChange(Project projectToBeUpdated) {
        if (!projectToBeUpdated.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            return;
        }

        projectToBeUpdated.getChildren().forEach(subproject -> {
            subproject.setVisibility(ProjectVisibility.PRIVATE);

            manageVisibilityChange(subproject);
        });
    }

    private void manageFinishedProject(Project projectToBeUpdated) {
        if (!projectToBeUpdated.isStatusFinished()) {
            return;
        }

        validator.verifySubprojectStatus(projectToBeUpdated);

        Project parent = projectToBeUpdated.getParentProject();
        if (parent == null) {
            return;
        }

        long finishedSubprojectsCount = parent.getChildren().stream().filter(Project::isStatusFinished).count();

        if (finishedSubprojectsCount != parent.getChildren().size()) {
            return;
        }

        List<Long> projectMembers = parent.getTeams().stream().flatMap(team -> team.getTeamMembers().stream()).map(TeamMember::getId).toList();

        MomentDto allSubprojectsDoneMoment = MomentDto.builder().name(ALL_SUBPROJECTS_DONE_MOMENT_NAME).projects(List.of(parent.getId())).userIds(projectMembers).build();
        momentService.createMoment(allSubprojectsDoneMoment);
    }

    private void fillProject(Project saved, ProjectDto projectDto) {
        Long parentProjectId = projectDto.getParentProjectId();
        if (parentProjectId != null) {
            saved.setParentProject(projectRepository.getProjectById(parentProjectId));
        }

        List<@NotNull Long> children = projectDto.getChildren();
        if (children != null && !children.isEmpty()) {
            saved.setChildren(projectRepository.findAllByIds(children));
        }
    }
}
