package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.IllegalSubProjectsStatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubProjectService {

    private final MomentService momentService;
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        Project parentProject = projectRepository.getProjectById(createSubProjectDto.getParentProjectId());
        projectValidator.validateSubProjectVisibility(parentProject.getVisibility(),
                createSubProjectDto.getVisibility());

        Project newChildProject = projectMapper.toEntity(createSubProjectDto);
        newChildProject.setParentProject(parentProject);
        newChildProject.setStatus(ProjectStatus.CREATED);

        return projectMapper.toDto(projectRepository.save(newChildProject));
    }

    @Transactional
    public ProjectDto updateProject(long projectId, ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectId);
        List<Project> allSubProjects = projectRepository.getAllSubProjectsFor(projectId);
        changeProjectStatus(project, allSubProjects, projectDto);
        changeProjectVisibility(project, allSubProjects, projectDto);
        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getSubProjects(long projectId, ProjectFilterDto projectFilterDto) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Project> allMatchedSubProjects = project.getChildren().stream()
                .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);
        for (ProjectFilter projectFilter : projectFilters) {
            allMatchedSubProjects = projectFilter.filter(allMatchedSubProjects, projectFilterDto);
        }
        return allMatchedSubProjects.map(projectMapper::toDto).toList();
    }

    private void changeProjectStatus(Project project, List<Project> allSubProjects, ProjectDto projectDto) {
        boolean areProjectStatusesMatched = allSubProjects.stream()
                .allMatch(subProject -> subProject.getStatus() == projectDto.getStatus());
        if (!areProjectStatusesMatched) {
            throw new IllegalSubProjectsStatusException(project.getId(), projectDto.getStatus());
        }
        project.setStatus(projectDto.getStatus());
        if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            createMomentAllSubtasksAreCompleted(project);
        }
    }

    private void changeProjectVisibility(Project project, List<Project> allSubProjects, ProjectDto projectDto) {
        if (projectDto.getVisibility() == ProjectVisibility.PRIVATE) {
            for (Project subProject : allSubProjects) {
                subProject.setVisibility(projectDto.getVisibility());
            }
        }
        project.setVisibility(projectDto.getVisibility());
    }

    private void createMomentAllSubtasksAreCompleted(Project project) {
        String momentName = "All subtasks are completed";
        String momentDescription = String.format("All subtasks of project %d were completed", project.getId());
        MomentDto momentDto = MomentDto.builder()
                .name(momentName)
                .projectsIds(List.of(project.getId()))
                .userIds(project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream().map(TeamMember::getUserId)).toList())
                .description(momentDescription)
                .date(LocalDateTime.now())
                .build();
        momentService.createMoment(momentDto);
    }
}
